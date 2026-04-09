const Payment = require('../models/Payment');
const Appointment = require('../models/Appointment');

// @desc    Initiate payment (Escrow Hold)
// @route   POST /api/payments/initiate
// @access  Private
exports.initiatePayment = async (req, res) => {
    try {
        const { appointmentId, amount, paymentMethod } = req.body;

        // Verify appointment exists
        const appointment = await Appointment.findById(appointmentId);
        if (!appointment) {
            return res.status(404).json({ success: false, message: 'Appointment not found' });
        }

        // Generate 6-digit release OTP
        const releaseOtp = Math.floor(100000 + Math.random() * 900000).toString();

        const payment = await Payment.create({
            user: req.user.id,
            appointment: appointmentId,
            amount,
            paymentMethod,
            transactionId: 'TXN' + Date.now(),
            releaseOtp // This will be shared with the user to give to the stylist
        });

        res.status(201).json({
            success: true,
            data: {
                paymentId: payment._id,
                status: payment.status,
                transactionId: payment.transactionId,
                message: "Payment held in escrow. Share OTP after service completion."
            },
            otp: releaseOtp // In production, this would be sent via SMS/Email to the user
        });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};

// @desc    Release funds to stylist (OTP Based)
// @route   POST /api/payments/release/:id
// @access  Private (Stylist/Admin)
exports.releasePayment = async (req, res) => {
    try {
        const { otp } = req.body;
        const payment = await Payment.findById(req.params.id).select('+releaseOtp');

        if (!payment) {
            return res.status(404).json({ success: false, message: 'Payment record not found' });
        }

        if (payment.status !== 'escrow') {
            return res.status(400).json({ success: false, message: `Payment is already ${payment.status}` });
        }

        if (payment.releaseOtp !== otp) {
            return res.status(400).json({ success: false, message: 'Invalid release OTP' });
        }

        payment.status = 'released';
        payment.releaseOtp = undefined; // Clear OTP after release
        await payment.save();

        // Also mark appointment as completed
        await Appointment.findByIdAndUpdate(payment.appointment, { status: 'completed' });

        res.status(200).json({
            success: true,
            message: 'Funds released successfully to the professional.'
        });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};

// @desc    Get my payments
// @route   GET /api/payments/my
// @access  Private
exports.getMyPayments = async (req, res) => {
    try {
        const payments = await Payment.find({ user: req.user.id }).populate('appointment');
        res.status(200).json({ success: true, count: payments.length, data: payments });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};
