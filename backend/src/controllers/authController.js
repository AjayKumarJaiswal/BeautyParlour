const User = require('../models/User');
const jwt = require('jsonwebtoken');
const { OAuth2Client } = require('google-auth-library');
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

// @desc    Register user
// @route   POST /api/auth/register
// @access  Public
exports.register = async (req, res) => {
    try {
        const { name, email, password, phoneNumber } = req.body;

        const userExists = await User.findOne({ $or: [{ email }, { phoneNumber }] });

        if (userExists) {
            return res.status(400).json({ message: 'User already exists with this email or phone' });
        }

        const user = await User.create({
            name,
            email,
            password,
            phoneNumber,
            isFirstTime: true
        });

        sendTokenResponse(user, 201, res);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// @desc    Login user
// @route   POST /api/auth/login
// @access  Public
exports.login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ message: 'Please provide an email and password' });
        }

        const user = await User.findOne({ email }).select('+password');

        if (!user) {
            return res.status(401).json({ message: 'Invalid credentials' });
        }

        const isMatch = await user.matchPassword(password);

        if (!isMatch) {
            return res.status(401).json({ message: 'Invalid credentials' });
        }

        sendTokenResponse(user, 200, res);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// @desc    Google Login
// @route   POST /api/auth/google
// @access  Public
exports.googleLogin = async (req, res) => {
    try {
        const { idToken } = req.body;

        const ticket = await client.verifyIdToken({
            idToken,
            audience: process.env.GOOGLE_CLIENT_ID,
        });

        const { name, email, picture } = ticket.getPayload();

        let user = await User.findOne({ email });
        let isFirstTime = false;

        if (!user) {
            isFirstTime = true;
            user = await User.create({
                name,
                email,
                password: Math.random().toString(36).slice(-8),
                phoneNumber: 'Not Provided',
                isFirstTime: true
            });
        }

        sendTokenResponse(user, 200, res);
    } catch (error) {
        res.status(400).json({ message: 'Google authentication failed' });
    }
};

// @desc    Send OTP
// @route   POST /api/auth/send-otp
// @access  Public
exports.sendOtp = async (req, res) => {
    try {
        const { phoneNumber } = req.body;
        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        // In real world, send SMS here. For now, we return it or store in DB/Cache
        res.status(200).json({
            success: true,
            message: `OTP sent to ${phoneNumber}`,
            otp: otp // Returning OTP for development/testing
        });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// @desc    Verify OTP
// @route   POST /api/auth/verify-otp
// @access  Public
exports.verifyOtp = async (req, res) => {
    try {
        const { phoneNumber, otp, serverOtp } = req.body;

        if (otp !== serverOtp) {
            return res.status(400).json({ message: 'Invalid OTP' });
        }

        let user = await User.findOne({ phoneNumber });
        let isFirstTime = false;

        if (!user) {
            isFirstTime = true;
            user = await User.create({
                name: 'New User',
                email: `${phoneNumber}@temp.com`,
                password: Math.random().toString(36).slice(-8),
                phoneNumber,
                isFirstTime: true
            });
        }

        sendTokenResponse(user, 200, res);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// @desc    Update First Time Status
// @route   PUT /api/auth/complete-onboarding
// @access  Private
exports.completeOnboarding = async (req, res) => {
    try {
        const user = await User.findByIdAndUpdate(req.user.id, { isFirstTime: false }, { new: true });
        res.status(200).json({ success: true, data: user });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

const sendTokenResponse = (user, statusCode, res) => {
    const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
        expiresIn: process.env.JWT_EXPIRE
    });

    res.status(statusCode).json({
        success: true,
        token,
        isFirstTime: user.isFirstTime,
        user: {
            id: user._id,
            name: user.name,
            email: user.email,
            phoneNumber: user.phoneNumber
        }
    });
};
