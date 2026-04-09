const mongoose = require('mongoose');

const serviceSchema = new mongoose.Schema({
    name: {
        type: String,
        required: [true, 'Please add a service name'],
        trim: true
    },
    category: {
        type: String,
        required: [true, 'Please add a category'],
        enum: ['Hair Cut', 'Makeup', 'Facial', 'Bridal']
    },
    price: {
        type: Number,
        required: [true, 'Please add a price']
    },
    description: {
        type: String,
        required: [true, 'Please add a description']
    },
    imageUrl: {
        type: String,
        default: 'no-image.jpg'
    },
    duration: {
        type: String,
        default: '30 mins'
    },
    createdAt: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Service', serviceSchema);
