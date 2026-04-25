"""Integration tests for migration_scripts/extract_po_data.py.

The full extraction loop requires a Mendix snapshot DB; gate it on PO_EXTRACT_DB
so the test stays runnable on dev boxes without that snapshot.
"""
import os
import re
import subprocess
import sys
from pathlib import Path

import pytest

REPO_ROOT = Path(__file__).resolve().parent.parent
SCRIPT = REPO_ROOT / "migration_scripts" / "extract_po_data.py"
OUTPUT = REPO_ROOT / "backend/src/main/resources/db/migration/V81__data_auctions_purchase_order.sql"


@pytest.fixture(autouse=True)
def cleanup_v81():
    """Wipe V81 before/after to avoid leaking generated SQL across runs."""
    if OUTPUT.exists():
        OUTPUT.unlink()
    yield
    if OUTPUT.exists():
        OUTPUT.unlink()


def test_script_runs_against_qa(monkeypatch):
    if not os.environ.get("PO_EXTRACT_DB"):
        pytest.skip("Set PO_EXTRACT_DB=qa-0327 to run extractor IT")
    result = subprocess.run(
        [sys.executable, str(SCRIPT), "--source-db", os.environ["PO_EXTRACT_DB"]],
        capture_output=True, text=True, cwd=REPO_ROOT,
    )
    assert result.returncode == 0, result.stderr
    assert OUTPUT.exists()
    content = OUTPUT.read_text()
    po_inserts = re.findall(r"INSERT INTO auctions\.purchase_order", content)
    assert len(po_inserts) >= 1
    pod_inserts = re.findall(r"INSERT INTO auctions\.po_detail", content)
    assert len(pod_inserts) >= 1
    assert "legacy_id" in content


def test_script_with_invalid_db_fails():
    result = subprocess.run(
        [sys.executable, str(SCRIPT), "--source-db", "nonexistent-db"],
        capture_output=True, text=True, cwd=REPO_ROOT,
    )
    assert result.returncode != 0
    stderr_lower = result.stderr.lower()
    # Either argparse choices rejection or psycopg2 connection failure is acceptable.
    assert (
        "could not connect" in stderr_lower
        or "fatal" in stderr_lower
        or "invalid choice" in stderr_lower
    )
