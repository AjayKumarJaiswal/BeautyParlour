const express = require('express');
const {
    bookAppointment,
    getMyAppointments,
    getAllAppointments,
    updateAppointmentStatus
} = require('../controllers/appointmentController');

const router = express.Router();

const { protect, authorize } = require('../middleware/auth');

router
    .route('/')
    .get(protect, authorize('admin'), getAllAppointments)
    .post(protect, bookAppointment);

router
    .route('/my')
    .get(protect, getMyAppointments);

router
    .route('/:id')
    .put(protect, authorize('admin'), updateAppointmentStatus);

module.exports = router;
