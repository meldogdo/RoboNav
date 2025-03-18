require('dotenv').config();
const jwt = require('jsonwebtoken'); 
const bcrypt = require('bcrypt');
const crypto = require('crypto');
const db = require('../config/db');
const logger = require('../utils/logger');
const { generateOTP, createTransporter } = require('../config/auth');

const SECRET_KEY = process.env.JWT_SECRET || 'your_jwt_secret';

const registerUser = async (req, res) => {
    const { username, password, email } = req.body;

    if (!username || !password || !email) {
        return res.status(400).json({ message: 'Username, email, and password are required' });
    }

    try {
        // Check if email or username already exists
        db.query('SELECT * FROM users WHERE email = ? OR username = ?', [email, username], async (err, results) => {
            if (err) {
                logger.error('Database error during user registration:', err);
                return res.status(500).json({ message: 'Database error', error: err });
            }

            if (results.length > 0) {
                let emailExists = false;
                let usernameExists = false;
                results.forEach(user => {
                    if (user.email === email) emailExists = true;
                    if (user.username === username) usernameExists = true;
                });

                if (emailExists && usernameExists) {
                    return res.status(400).json({ message: 'Email and username already in use' });
                } else if (emailExists) {
                    return res.status(400).json({ message: 'Email already in use' });
                } else if (usernameExists) {
                    return res.status(400).json({ message: 'Username already in use' });
                }
            }

            // Hash password
            const hashedPassword = await bcrypt.hash(password, 10);

            // Insert user into database with confirmed = 0 (not confirmed)
            db.query('INSERT INTO users (username, hashed_password, email, confirmed) VALUES (?, ?, ?, 0)',
                [username, hashedPassword, email],
                async (err, result) => {
                    if (err) {
                        logger.error('Error inserting user into database:', err);
                        return res.status(500).json({ message: 'Error inserting user', error: err });
                    }

                    // Generate token and set expiration
                    const token = crypto.randomBytes(32).toString('hex');
                    const expiresAt = new Date(Date.now() + 24 * 60 * 60 * 1000); // 24 hours expiry

                    const confirmationLink = `http://localhost:8080/api/open/users/confirm-email?token=${token}`;

                    // Save token with expiry in the database
                    db.query('INSERT INTO email_confirmations (user_id, token, expires_at) VALUES (?, ?, ?)',
                        [result.insertId, token, expiresAt]);

                    // Send the confirmation email
                    const mailOptions = {
                        from: process.env.EMAIL_USER,
                        to: email,
                        subject: 'Confirm your email',
                        text: `Click the link to confirm your email: ${confirmationLink} (Expires in 24 hours)`
                    };

                    try {
                        const transporter = await createTransporter();
                        transporter.sendMail(mailOptions, (error) => {
                            if (error) {
                                logger.error('Error sending email confirmation:', error);
                                return res.status(500).json({ message: 'Error sending email', error });
                            }
                            res.status(201).json({ message: 'Registration successful. Check your email to confirm.' });
                        });
                    } catch (emailError) {
                        logger.error('Error setting up email transporter:', emailError);
                        return res.status(500).json({ message: 'Error setting up email transporter', error: emailError });
                    }
                }
            );
        });
    } catch (error) {
        logger.error('Unexpected error during user registration:', error);
        res.status(500).json({ message: 'Error processing request', error });
    }
};

// Email Confirmation
const confirmEmail = (req, res) => {
    const { token } = req.query;

    if (!token) {
        logger.warn('Email confirmation attempt with no token provided.');
        return res.status(400).json({ message: 'No token provided' });
    }

    logger.info(`Processing email confirmation for token: ${token}`);

    db.query('SELECT * FROM email_confirmations WHERE token = ? AND expires_at > NOW()', [token], (err, results) => {
        if (err) {
            logger.error('Database error while checking email confirmation token:', err);
            return res.status(500).json({ message: 'Database error', error: err });
        }

        if (results.length === 0) {
            logger.warn(`Invalid or expired email confirmation token: ${token}`);
            return res.status(400).json({ message: 'Invalid or expired token' });
        }

        const userId = results[0].user_id;

        db.query('UPDATE users SET confirmed = 1 WHERE id = ?', [userId], (err) => {
            if (err) {
                logger.error(`Database error while updating user confirmation for user ID ${userId}:`, err);
                return res.status(500).json({ message: 'Error updating user', error: err });
            }

            db.query('DELETE FROM email_confirmations WHERE token = ?', [token], (deleteErr) => {
                if (deleteErr) {
                    logger.error(`Failed to delete email confirmation token for user ID ${userId}:`, deleteErr);
                }
            });

            logger.info(`Email successfully confirmed for user ID ${userId}.`);
            res.status(200).json({ message: 'Email confirmed successfully. You can now log in.' });
        });
    });
};

// User Login
const loginUser = (req, res) => {
    const { username, password } = req.body;

    if (!username || !password) {
        logger.warn('Login attempt with missing credentials.');
        return res.status(400).json({ message: 'Username and password are required' });
    }

    logger.info(`User login attempt: ${username}`);

    db.query('SELECT * FROM users WHERE username = ?', [username], async (err, results) => {
        if (err) {
            logger.error(`Database error during login for user: ${username}`, err);
            return res.status(500).json({ message: 'Database error', error: err });
        }

        if (results.length === 0) {
            logger.warn(`Invalid login attempt: User ${username} not found.`);
            return res.status(401).json({ message: 'Invalid credentials' });
        }

        const user = results[0];

        // Check if account is disabled
        if (user.confirmed === 2) {
            logger.warn(`Disabled account login attempt: ${username}`);
            return res.status(403).json({ message: 'Account disabled. Contact support.' });
        }

        // Check if email is confirmed
        if (user.confirmed === 0) {
            logger.warn(`Unconfirmed account login attempt: ${username}`);
            return res.status(401).json({ message: 'Confirm your email before logging in' });
        }

        try {
            const passwordMatch = await bcrypt.compare(password, user.hashed_password);

            if (!passwordMatch) {
                logger.warn(`Invalid password attempt for user: ${username}`);
                return res.status(401).json({ message: 'Invalid credentials' });
            }

            // Generate JWT Token
            const token = jwt.sign({ userId: user.id, username: user.username }, SECRET_KEY, { expiresIn: '1h' });

            logger.info(`User login successful: ${username}`);
            res.json({ message: 'Login successful', token });

        } catch (error) {
            logger.error(`Error during password comparison for user: ${username}`, error);
            return res.status(500).json({ message: 'Error processing request', error });
        }
    });
};

// Request Password Reset
const requestPasswordReset = async (req, res) => {
    const { email } = req.body;

    if (!email) {
        logger.warn('Password reset request received with missing email.');
        return res.status(400).json({ message: 'Email is required' });
    }

    logger.info(`Processing password reset request for email: ${email}`);

    // Check if email exists and is confirmed in DB
    db.query('SELECT email, confirmed FROM users WHERE email = ?', [email], (err, results) => {
        if (err) {
            logger.error(`Database query error while checking email: ${email}`, err);
            return res.status(500).json({ message: 'Database error', error: err });
        }
        if (results.length === 0) {
            logger.warn(`Password reset attempt for non-existent email: ${email}`);
            return res.status(404).json({ message: 'Email not found' });
        }

        const user = results[0];

        // Check confirmation status
        if (user.confirmed === 0) {
            logger.warn(`Password reset denied for unconfirmed email: ${email}`);
            return res.status(403).json({ message: 'Confirm your email before resetting.' });
        }

        if (user.confirmed === 2) {
            logger.warn(`Password reset denied for disabled account: ${email}`);
            return res.status(403).json({ message: 'This account is disabled. Please contact support.' });
        }

        logger.info(`Email confirmed for password reset: ${email}`);

        // Generate a 6-digit OTP and expiration time
        const resetCode = generateOTP();
        const expiresAt = new Date(Date.now() + 5 * 60000); // Expires in 5 minutes

        logger.info(`Generated OTP for ${email}: ${resetCode}`);

        // Save OTP to DB
        db.query(
            'INSERT INTO password_reset_tokens (email, reset_code, expires_at) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE reset_code = ?, expires_at = ?',
            [email, resetCode, expiresAt, resetCode, expiresAt],
            async (err) => {
                if (err) {
                    logger.error(`Error inserting OTP for ${email}:`, err);
                    return res.status(500).json({ message: 'Database error', error: err });
                }

                logger.info(`Reset code saved in database for: ${email}`);

                // Send email
                try {
                    const transporter = await createTransporter();
                    const mailOptions = {
                        from: process.env.EMAIL_USER,
                        to: email,
                        subject: 'Password Reset Code',
                        text: `Your password reset code is: ${resetCode}. This code expires in 5 minutes.`,
                    };

                    transporter.sendMail(mailOptions, (error) => {
                        if (error) {
                            logger.error(`Error sending reset email to ${email}:`, error);
                            return res.status(500).json({ message: 'Error sending email', error });
                        }

                        logger.info(`Reset code sent successfully to: ${email}`);
                        res.json({ message: 'Reset code sent to email' });
                    });

                } catch (emailError) {
                    logger.error(`Error setting up email transporter for ${email}:`, emailError);
                    return res.status(500).json({ message: 'Error setting up email transporter', error: emailError });
                }
            }
        );
    });
};

// Verify Password Reset
const verifyReset = (req, res) => {
    const { email, resetCode } = req.body;

    if (!email || !resetCode) {
        logger.warn('Password reset verification attempt with missing email or reset code.');
        return res.status(400).json({ message: 'Email and reset code are required' });
    }

    logger.info(`Verifying reset code for email: ${email}`);

    // Check if OTP is valid and not expired
    db.query(
        'SELECT * FROM password_reset_tokens WHERE email = ? AND reset_code = ? AND expires_at > NOW()',
        [email, resetCode],
        (err, results) => {
            if (err) {
                logger.error(`Database query error while verifying reset code for ${email}:`, err);
                return res.status(500).json({ message: 'Database error', error: err });
            }

            if (results.length === 0) {
                logger.warn(`Invalid or expired reset code attempt for email: ${email}`);
                return res.status(400).json({ message: 'Invalid or expired reset code' });
            }

            logger.info(`Reset code verified successfully for: ${email}`);

            // Generate JWT token to authenticate reset password request
            const token = jwt.sign({ email }, SECRET_KEY, { expiresIn: '10m' });

            // Delete OTP from DB after verification
            db.query('DELETE FROM password_reset_tokens WHERE email = ?', [email], (deleteErr) => {
                if (deleteErr) {
                    logger.error(`Error deleting used reset token for ${email}:`, deleteErr);
                }
            });

            res.json({ message: 'Reset code verified', token });
        }
    );
};

// Reset Password
const resetPassword = async (req, res) => {
    const { new_password } = req.body;

    if (!new_password) {
        logger.warn('Password reset attempt with missing new_password.');
        return res.status(400).json({ message: 'New password is required' });
    }

    const email = req.user.email; // Extract email from authentication middleware
    logger.info(`Password reset attempt for user: ${email}`);

    try {
        const hashedPassword = await bcrypt.hash(new_password, 10);

        db.query('UPDATE users SET hashed_password = ? WHERE email = ?', [hashedPassword, email], (err) => {
            if (err) {
                logger.error(`Database error while resetting password for user: ${email}`, err);
                return res.status(500).json({ message: 'Database error', error: err });
            }

            logger.info(`Password reset successful for user: ${email}`);
            res.json({ message: 'Password reset successful' });
        });

    } catch (error) {
        logger.error(`Error hashing new password for user: ${email}`, error);
        return res.status(500).json({ message: 'Error processing request', error });
    }
};

// Change Password
const changePassword = async (req, res) => {
    const { old_password, new_password } = req.body;
    const userId = req.user.userId; // Extracted from JWT token

    if (!old_password || !new_password) {
        logger.warn(`User ${userId} attempted password change with missing fields.`);
        return res.status(400).json({ message: 'Old and new password are required' });
    }

    logger.info(`User ${userId} attempting to change password.`);

    // Retrieve user from database
    db.query('SELECT * FROM users WHERE id = ?', [userId], async (err, results) => {
        if (err) {
            logger.error(`Database error while fetching user ${userId}:`, err);
            return res.status(500).json({ message: 'Database error', error: err });
        }

        if (results.length === 0) {
            logger.warn(`Password change attempt for non-existent user ID: ${userId}`);
            return res.status(404).json({ message: 'User not found' });
        }

        const user = results[0];

        try {
            // Verify old password
            const passwordMatch = await bcrypt.compare(old_password, user.hashed_password);
            if (!passwordMatch) {
                logger.warn(`User ${userId} entered incorrect current password.`);
                return res.status(403).json({ message: 'Incorrect current password' });
            }

            // Hash new password
            const hashedPassword = await bcrypt.hash(new_password, 10);

            // Update password in database
            db.query('UPDATE users SET hashed_password = ? WHERE id = ?', [hashedPassword, userId], (updateErr) => {
                if (updateErr) {
                    logger.error(`Error updating password for user ${userId}:`, updateErr);
                    return res.status(500).json({ message: 'Error updating password', error: updateErr });
                }

                logger.info(`Password successfully changed for user ${userId}.`);
                res.json({ message: 'Password changed successfully' });
            });

        } catch (hashError) {
            logger.error(`Error hashing new password for user ${userId}:`, hashError);
            return res.status(500).json({ message: 'Error processing request', error: hashError });
        }
    });
};

module.exports = {
    registerUser,
    confirmEmail,
    loginUser,
    requestPasswordReset,
    verifyReset,
    resetPassword,
    changePassword
};
