"use client";

import { useRouter } from "next/navigation";
import ReserveBidUploadForm from "../ReserveBidUploadForm";
import styles from "../reserveBidForm.module.css";

const LIST_PATH = "/admin/auctions-data-center/reserve-bids";

/**
 * Deep-link upload route. Kept for users who bookmark `/upload`; the primary
 * UX is now the upload modal triggered from the list page (RB-18). Both
 * surfaces share ReserveBidUploadForm.
 */
export default function UploadReserveBidsPage() {
  const router = useRouter();
  return (
    <div className={styles.page}>
      <h1 className={styles.heading}>Upload EB Price</h1>
      <ReserveBidUploadForm
        secondaryLabel="Back"
        onSecondary={() => router.push(LIST_PATH)}
        onUploaded={() => {
          // No auto-redirect — leave the user on the result panel so they can
          // review created/updated/error counts before navigating back.
        }}
      />
    </div>
  );
}
