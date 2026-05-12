// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { PhotoUploadDropzone } from './PhotoUploadDropzone';

// Stub the client module so the test can drive uploadPhoto's outcome.
vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return {
    ...actual,
    uploadPhoto: vi.fn(),
  };
});

import { uploadPhoto, PhotoUploadError } from '@/lib/partialCreditClient';

const mockUpload = uploadPhoto as unknown as ReturnType<typeof vi.fn>;

function makeImageFile(name: string, type: string, sizeBytes: number): File {
  return new File([new Uint8Array(sizeBytes)], name, { type });
}

describe('PhotoUploadDropzone', () => {
  beforeEach(() => {
    mockUpload.mockReset();
  });

  it('uploads a single valid image and notifies onUploaded', async () => {
    mockUpload.mockResolvedValueOnce({
      id: 1,
      creditRequestId: 100,
      wrongDeviceLineId: 500,
      kind: 'WRONG_DEVICE',
      originalFilename: 'shot.jpg',
      contentType: 'image/jpeg',
      sizeBytes: 1024,
      uploadedDate: null,
      uploadedByUserId: 7,
    });
    const onUploaded = vi.fn();
    render(
      <PhotoUploadDropzone
        requestId={100}
        wrongDeviceLineId={500}
        onUploaded={onUploaded}
      />,
    );

    const input = screen.getByLabelText('Upload photos') as HTMLInputElement;
    fireEvent.change(input, {
      target: { files: [makeImageFile('shot.jpg', 'image/jpeg', 1024)] },
    });

    await waitFor(() => {
      expect(mockUpload).toHaveBeenCalledWith(100, expect.any(File), 500);
      expect(onUploaded).toHaveBeenCalledTimes(1);
    });
  });

  it('rejects non-image MIMEs client-side without calling uploadPhoto', async () => {
    const onUploaded = vi.fn();
    render(
      <PhotoUploadDropzone requestId={100} onUploaded={onUploaded} />,
    );

    fireEvent.change(screen.getByLabelText('Upload photos'), {
      target: { files: [makeImageFile('doc.pdf', 'application/pdf', 100)] },
    });

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent(
        /only JPEG \/ PNG \/ HEIC \/ WebP are allowed/i,
      );
    });
    expect(mockUpload).not.toHaveBeenCalled();
    expect(onUploaded).not.toHaveBeenCalled();
  });

  it('surfaces the server-side PhotoUploadError message inline', async () => {
    mockUpload.mockRejectedValueOnce(
      new PhotoUploadError('TOO_LARGE', 'File exceeds 5 MB limit', 413),
    );
    render(<PhotoUploadDropzone requestId={100} onUploaded={vi.fn()} />);

    fireEvent.change(screen.getByLabelText('Upload photos'), {
      target: { files: [makeImageFile('big.jpg', 'image/jpeg', 1024)] },
    });

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('File exceeds 5 MB limit');
    });
  });

  it('uploads sequentially when multiple files are selected (avoid per-line cap race)', async () => {
    mockUpload
      .mockResolvedValueOnce({
        id: 1,
        creditRequestId: 100,
        wrongDeviceLineId: 500,
        kind: 'WRONG_DEVICE',
        originalFilename: 'a.jpg',
        contentType: 'image/jpeg',
        sizeBytes: 100,
        uploadedDate: null,
        uploadedByUserId: 7,
      })
      .mockResolvedValueOnce({
        id: 2,
        creditRequestId: 100,
        wrongDeviceLineId: 500,
        kind: 'WRONG_DEVICE',
        originalFilename: 'b.jpg',
        contentType: 'image/jpeg',
        sizeBytes: 100,
        uploadedDate: null,
        uploadedByUserId: 7,
      });
    const onUploaded = vi.fn();
    render(
      <PhotoUploadDropzone
        requestId={100}
        wrongDeviceLineId={500}
        onUploaded={onUploaded}
      />,
    );

    fireEvent.change(screen.getByLabelText('Upload photos'), {
      target: {
        files: [
          makeImageFile('a.jpg', 'image/jpeg', 100),
          makeImageFile('b.jpg', 'image/jpeg', 100),
        ],
      },
    });

    await waitFor(() => {
      expect(mockUpload).toHaveBeenCalledTimes(2);
      expect(onUploaded).toHaveBeenCalledTimes(2);
    });
  });
});
