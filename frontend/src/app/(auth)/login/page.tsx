import LoginForm from './LoginForm';
import styles from './login.module.css';

export const metadata = {
  title: 'Login - ecoATM Sales Platform',
  description: 'Authentication for ecoATM internal and external users.',
};

export default function LoginPage() {
  return (
    <div className={styles.container}>
      <LoginForm />
    </div>
  );
}
