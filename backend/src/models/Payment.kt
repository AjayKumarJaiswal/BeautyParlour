const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.ObjectId,
        ref: 'User',
        required: true
    },
    appointment: {
        type: mongoose.Schema.ObjectId,
        ref: 'Appointment',
        required: true
    },
    amount: {
        type: Number,
        required: [true, 'Please add an amount']
    },
    paymentMethod: {
        type: String,
        required: [true, 'Please add a payment method'],
        enum: ['UPI', 'Card', 'NetBanking', 'Wallet']
    },
    status: {
        type: String,
        enum: ['escrow', 'released', 'refunded'],
        default: 'escrow'
    },
    releaseOtp: {
        type: String,
        select: false
    },
    transactionId: {
        type: String,
        unique: true,
        required: true
    },
    createdAt: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Payment', paymentSchema);
