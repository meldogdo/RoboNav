const { google } = require('googleapis');
const nodemailer = require('nodemailer');
const dotenv = require('dotenv');

dotenv.config();

// Environment variables
const SECRET_KEY = process.env.JWT_SECRET || 'your_jwt_secret';
const CLIENT_ID = process.env.CLIENT_ID;
const EMAIL_USER = process.env.EMAIL_USER;
const CLIENT_SECRET = process.env.CLIENT_SECRET;
const REDIRECT_URI = 'https://developers.google.com/oauthplayground';
const REFRESH_TOKEN = process.env.REFRESH_TOKEN;

// Initialize OAuth2 client
const oauth2Client = new google.auth.OAuth2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
oauth2Client.setCredentials({ refresh_token: REFRESH_TOKEN });

// Retrieve OAuth2 access token
async function getAccessToken() {
  const { token } = await oauth2Client.getAccessToken();
  return token;
}

// Configure and return a Nodemailer transporter
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
      accessToken,
    },
  });
}

// Generate a random n-digit OTP
function generateOTP(length = 6) {
  return Array.from({ length }, () => Math.floor(Math.random() * 10)).join('');
}

module.exports = { createTransporter, generateOTP };
