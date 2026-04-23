import { Suspense } from 'react';
import ResetPasswordForm from './ResetPasswordForm';
import styles from './resetPassword.module.css';

export const metadata = {
  title: 'Reset Password - ecoATM Sales Platform',
  description: 'Set a new password for your ecoATM Sales Platform account.',
};

export default function ResetPasswordPage() {
  return (
    <div className={styles.container}>
      {/* Suspense required because ResetPasswordForm calls useSearchParams() */}
      <Suspense>
        <ResetPasswordForm />
      </Suspense>
    </div>
  );
}
