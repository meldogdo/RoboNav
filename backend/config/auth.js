const { google } = require('googleapis');
const nodemailer = require('nodemailer');
const dotenv = require('dotenv');

dotenv.config();

const SECRET_KEY = process.env.JWT_SECRET || 'your_jwt_secret';
const CLIENT_ID = process.env.CLIENT_ID;
const EMAIL_USER = process.env.EMAIL_USER;
const CLIENT_SECRET = process.env.CLIENT_SECRET;
const REDIRECT_URI = 'https://developers.google.com/oauthplayground';
const REFRESH_TOKEN = process.env.REFRESH_TOKEN;

// Create an OAuth2 client
const oauth2Client = new google.auth.OAuth2(
  CLIENT_ID,
  CLIENT_SECRET,
  REDIRECT_URI
);

oauth2Client.setCredentials({ refresh_token: REFRESH_TOKEN });

// Get access token
async function getAccessToken() {
  const { token } = await oauth2Client.getAccessToken();
  return token;
}

// Create a transporter using OAuth2
async function createTransporter() {
  const accessToken = await getAccessToken();

  return nodemailer.createTransport({
    service: 'gmail',
    auth: {
      type: 'OAuth2',
      user: EMAIL_USER,
      clientId: CLIENT_ID,
      clientSecret: CLIENT_SECRET,
      refreshToken: REFRESH_TOKEN,
      accessToken: accessToken,
    },
  });
}

// 🔹 Generate a 6-digit OTP
function generateOTP(length = 6) {
  let otp = '';
  for (let i = 0; i < length; i++) {
    otp += Math.floor(Math.random() * 10); // Generate a random digit (0-9)
  }
  return otp;
}

module.exports = { createTransporter, generateOTP };
