// @vitest-environment jsdom
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach } from 'vitest';
import { PhotoGallery } from './PhotoGallery';
import type { PhotoMetadata } from '@/lib/partialCreditClient';

vi.mock('@/lib/partialCreditClient', async () => {
  const actual = await vi.importActual<typeof import('@/lib/partialCreditClient')>(
    '@/lib/partialCreditClient',
  );
  return {
    ...actual,
    deletePhoto: vi.fn().mockResolvedValue(undefined),
  };
});

import { deletePhoto } from '@/lib/partialCreditClient';
const mockDelete = deletePhoto as unknown as ReturnType<typeof vi.fn>;

function photo(overrides: Partial<PhotoMetadata>): PhotoMetadata {
  return {
    id: 1,
    creditRequestId: 100,
    wrongDeviceLineId: null,
    kind: 'DAMAGE',
    originalFilename: 'shot.jpg',
    contentType: 'image/jpeg',
    sizeBytes: 1024,
    uploadedDate: '2026-05-12T00:00:00Z',
    uploadedByUserId: 7,
    ...overrides,
  };
}

describe('PhotoGallery', () => {
  beforeEach(() => {
    mockDelete.mockClear();
  });

  it('renders the empty-state message when there are no photos', () => {
    render(
      <PhotoGallery photos={[]} viewerUserId={7} readOnly={false} onDeleted={vi.fn()} />,
    );
    expect(screen.getByText(/No photos uploaded yet/i)).toBeInTheDocument();
  });

  it('renders a thumbnail per photo and includes the filename caption', () => {
    render(
      <PhotoGallery
        photos={[photo({ id: 1 }), photo({ id: 2, originalFilename: 'b.jpg' })]}
        viewerUserId={7}
        readOnly={false}
        onDeleted={vi.fn()}
      />,
    );
    // Two thumbnails with matching alt text + filenames in captions.
    expect(screen.getAllByRole('img')).toHaveLength(2);
    expect(screen.getByText('shot.jpg')).toBeInTheDocument();
    expect(screen.getByText('b.jpg')).toBeInTheDocument();
  });

  it('shows Delete only on the viewer\'s own photos', () => {
    render(
      <PhotoGallery
        photos={[
          photo({ id: 1, uploadedByUserId: 7, originalFilename: 'mine.jpg' }),
          photo({ id: 2, uploadedByUserId: 99, originalFilename: 'theirs.jpg' }),
        ]}
        viewerUserId={7}
        readOnly={false}
        onDeleted={vi.fn()}
      />,
    );
    expect(screen.getByLabelText('Delete mine.jpg')).toBeInTheDocument();
    expect(screen.queryByLabelText('Delete theirs.jpg')).not.toBeInTheDocument();
  });

  it('hides every Delete button when readOnly is true (request finalised)', () => {
    render(
      <PhotoGallery
        photos={[photo({ id: 1, uploadedByUserId: 7 })]}
        viewerUserId={7}
        readOnly={true}
        onDeleted={vi.fn()}
      />,
    );
    expect(screen.queryByLabelText(/^Delete/)).not.toBeInTheDocument();
  });

  it('calls deletePhoto + onDeleted when the trash button is clicked', async () => {
    const onDeleted = vi.fn();
    render(
      <PhotoGallery
        photos={[photo({ id: 42, uploadedByUserId: 7 })]}
        viewerUserId={7}
        readOnly={false}
        onDeleted={onDeleted}
      />,
    );

    fireEvent.click(screen.getByLabelText('Delete shot.jpg'));

    await waitFor(() => {
      expect(mockDelete).toHaveBeenCalledWith(42);
      expect(onDeleted).toHaveBeenCalledWith(42);
    });
  });

  it('opens a lightbox dialog when a thumbnail is clicked', () => {
    render(
      <PhotoGallery
        photos={[photo({ id: 1 })]}
        viewerUserId={7}
        readOnly={false}
        onDeleted={vi.fn()}
      />,
    );

    fireEvent.click(screen.getByLabelText('Open shot.jpg'));

    expect(screen.getByRole('dialog', { name: 'shot.jpg' })).toBeInTheDocument();
  });
});
