// @vitest-environment jsdom
import { renderHook, act } from '@testing-library/react';
import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import { useAutoSaveBid } from './useAutoSaveBid';
import * as bidderApi from '../lib/bidder';

describe('useAutoSaveBid', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => { vi.useRealTimers(); vi.restoreAllMocks(); });

  it('debounces rapid saves into a single API call after 500ms', async () => {
    const saveSpy = vi.spyOn(bidderApi, 'saveBid').mockResolvedValue({} as any);
    const onSaved = vi.fn();

    const { result } = renderHook(() => useAutoSaveBid(123, onSaved));

    act(() => {
      result.current.save({ bidQuantity: 1, bidAmount: 10 });
      result.current.save({ bidQuantity: 2, bidAmount: 20 });
      result.current.save({ bidQuantity: 3, bidAmount: 30 });
    });

    expect(saveSpy).not.toHaveBeenCalled();
    await act(async () => { vi.advanceTimersByTime(500); });
    expect(saveSpy).toHaveBeenCalledTimes(1);
    expect(saveSpy).toHaveBeenCalledWith(123, { bidQuantity: 3, bidAmount: 30 });
  });

  it('serializes blank quantity as null', async () => {
    const saveSpy = vi.spyOn(bidderApi, 'saveBid').mockResolvedValue({} as any);
    const { result } = renderHook(() => useAutoSaveBid(123, () => {}));

    act(() => { result.current.save({ bidQuantity: null, bidAmount: 10 }); });
    await act(async () => { vi.advanceTimersByTime(500); });

    expect(saveSpy).toHaveBeenCalledWith(123, { bidQuantity: null, bidAmount: 10 });
  });
});
