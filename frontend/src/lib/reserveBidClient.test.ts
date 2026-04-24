import { describe, expect, it, vi, beforeEach } from "vitest";
import { reserveBidClient } from "./reserveBidClient";

describe("reserveBidClient", () => {
  beforeEach(() => vi.restoreAllMocks());

  it("list builds query string from params", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true, status: 200,
      json: async () => ({ rows: [], total: 0, page: 0, size: 20 }),
    });
    global.fetch = fetchMock as never;

    await reserveBidClient.list({ productId: "77001", page: 2 });
    expect(fetchMock).toHaveBeenCalledOnce();
    const url = fetchMock.mock.calls[0][0];
    expect(url).toContain("productId=77001");
    expect(url).toContain("page=2");
  });

  it("upload sends multipart form-data", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true, status: 200,
      json: async () => ({ created: 0, updated: 0, unchanged: 0, auditsGenerated: 0, errors: [] }),
    });
    global.fetch = fetchMock as never;

    const file = new File(["x"], "t.xlsx");
    await reserveBidClient.upload(file);
    const init = fetchMock.mock.calls[0][1];
    expect(init.body).toBeInstanceOf(FormData);
  });
});
