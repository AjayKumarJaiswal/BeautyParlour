const Appointment = require('../models/Appointment');
const Service = require('../models/Service');

// @desc    Book an appointment
// @route   POST /api/appointments
// @access  Private
exports.bookAppointment = async (req, res) => {
    try {
        const { service, date, timeSlot } = req.body;

        // Check if service exists
        const serviceExists = await Service.findById(service);
        if (!serviceExists) {
            return res.status(404).json({ success: false, message: 'Service not found' });
        }

        // Validate date (cannot book in the past)
        const appointmentDate = new Date(date);
        const today = new Date();
        today.setHours(0,0,0,0);

        if (appointmentDate < today) {
            return res.status(400).json({ success: false, message: 'Cannot book an appointment in the past' });
        }

        // Check for double booking (same date and time slot)
        const existingAppointment = await Appointment.findOne({
            date: appointmentDate,
            timeSlot: timeSlot,
            status: { $ne: 'cancelled' }
        });

        if (existingAppointment) {
            return res.status(400).json({ success: false, message: 'This time slot is already booked' });
        }

        // Create appointment
        const appointment = await Appointment.create({
            user: req.user.id,
            service,
            date: appointmentDate,
            timeSlot
        });

        res.status(201).json({
            success: true,
            data: appointment
        });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};

// @desc    Get all appointments for logged in user
// @route   GET /api/appointments/my
// @access  Private
exports.getMyAppointments = async (req, res) => {
    try {
        const appointments = await Appointment.find({ user: req.user.id }).populate('service', 'name category price');
        res.status(200).json({
            success: true,
            count: appointments.length,
            data: appointments
        });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};

// @desc    Get all appointments (Admin only)
// @route   GET /api/appointments
// @access  Private/Admin
exports.getAllAppointments = async (req, res) => {
    try {
        const appointments = await Appointment.find().populate('user', 'name email').populate('service', 'name');
        res.status(200).json({
            success: true,
            count: appointments.length,
            data: appointments
        });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};

// @desc    Update appointment status
// @route   PUT /api/appointments/:id
// @access  Private/Admin
exports.updateAppointmentStatus = async (req, res) => {
    try {
        const appointment = await Appointment.findByIdAndUpdate(req.params.id, { status: req.body.status }, {
            new: true,
            runValidators: true
        });

        if (!appointment) {
            return res.status(404).json({ success: false, message: 'Appointment not found' });
        }

        res.status(200).json({ success: true, data: appointment });
    } catch (err) {
        res.status(400).json({ success: false, message: err.message });
    }
};
