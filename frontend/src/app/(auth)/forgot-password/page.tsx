import ForgotPasswordForm from './ForgotPasswordForm';
import styles from './forgotPassword.module.css';

export const metadata = {
  title: 'Forgot Password - ecoATM Sales Platform',
  description: 'Request a password reset link for your ecoATM Sales Platform account.',
};

export default function ForgotPasswordPage() {
  return (
    <div className={styles.container}>
      <ForgotPasswordForm />
    </div>
  );
}
