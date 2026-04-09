const app = require('./app');

const PORT = process.env.PORT || 5000;

// Listen on 0.0.0.0 to allow access from external devices on the same network
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server is running on port ${PORT}`);
});
