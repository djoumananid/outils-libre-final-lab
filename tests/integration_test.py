#!/usr/bin/env python3
"""
Integration tests for the PricingEngine.
Runs the compiled Java project via Gradle and verifies output.

Usage:
    python tests/integration_test.py

Requirements:
    - Java 11+
    - Gradle wrapper present in project root
"""

import subprocess
import sys
import os
import json
import unittest

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# ---------------------------------------------------------------------------
# Helper to call the Java engine via a small JSON-based CLI wrapper
# We compile & run using `gradle run` with arguments via -PappArgs
# ---------------------------------------------------------------------------

def run_gradle_test():
    """Run `gradle test` and return (returncode, stdout, stderr)."""
    gradlew = "./gradlew" if sys.platform != "win32" else "gradlew.bat"
    result = subprocess.run(
        [gradlew, "test", "--rerun-tasks"],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
        timeout=120,
    )
    return result.returncode, result.stdout, result.stderr


def run_gradle_build():
    """Compile the project. Returns (returncode, stdout, stderr)."""
    gradlew = "./gradlew" if sys.platform != "win32" else "gradlew.bat"
    result = subprocess.run(
        [gradlew, "build", "-x", "test"],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
        timeout=120,
    )
    return result.returncode, result.stdout, result.stderr


# ---------------------------------------------------------------------------
# Test cases
# ---------------------------------------------------------------------------

class TestGradleBuild(unittest.TestCase):
    """Verify the project compiles cleanly."""

    def test_build_succeeds(self):
        rc, stdout, stderr = run_gradle_build()
        self.assertEqual(
            rc, 0,
            msg=f"Build failed!\nSTDOUT:\n{stdout}\nSTDERR:\n{stderr}"
        )
        print("[PASS] Gradle build succeeded.")


class TestJUnitSuite(unittest.TestCase):
    """Run the full JUnit suite and confirm all tests pass."""

    def test_all_junit_tests_pass(self):
        rc, stdout, stderr = run_gradle_test()
        self.assertEqual(
            rc, 0,
            msg=f"JUnit tests failed!\nSTDOUT:\n{stdout}\nSTDERR:\n{stderr}"
        )
        # Confirm at least some tests ran (guard against misconfiguration)
        self.assertIn(
            "tests", stdout.lower(),
            msg="Could not confirm any tests ran — check Gradle output."
        )
        print("[PASS] All JUnit tests passed.")
        # Print a summary snippet
        for line in stdout.splitlines():
            if any(kw in line for kw in ["tests", "passed", "failed", "skipped", "BUILD"]):
                print("  ", line.strip())


class TestBusinessRules(unittest.TestCase):
    """
    Spot-check computed values against manually verified expectations.
    These drive a small Java CLI runner (Main.java) and parse its output.
    """

    @classmethod
    def setUpClass(cls):
        """Compile once before all business-rule tests."""
        rc, stdout, stderr = run_gradle_build()
        if rc != 0:
            raise RuntimeError(f"Build failed, cannot run business tests.\n{stderr}")

    def _run_main(self):
        """Run Main and return stdout lines."""
        gradlew = "./gradlew" if sys.platform != "win32" else "gradlew.bat"
        result = subprocess.run(
            [gradlew, "run", "-q"],
            cwd=PROJECT_ROOT,
            capture_output=True,
            text=True,
            timeout=60,
        )
        return result.stdout, result.stderr, result.returncode

    def test_main_runs_without_error(self):
        stdout, stderr, rc = self._run_main()
        self.assertEqual(rc, 0, msg=f"Main exited with error:\n{stderr}\n{stdout}")
        print("[PASS] Main.java ran without errors.")

    def test_main_output_contains_expected_sections(self):
        stdout, _, _ = self._run_main()
        self.assertIn("REGULAR", stdout)
        self.assertIn("VIP", stdout)
        self.assertIn("finalPrice", stdout)
        print("[PASS] Main output contains expected sections.")

    def test_regular_no_promo_final_price(self):
        """
        Order: 2×$29.99 + 3×$9.50 = $88.48 subtotal
        Tax REGULAR 8%: $88.48 × 1.08 = $95.56
        """
        stdout, _, rc = self._run_main()
        self.assertEqual(rc, 0)
        # Look for the finalPrice line in the REGULAR section
        lines = stdout.splitlines()
        in_regular = False
        final_price = None
        for line in lines:
            if "REGULAR" in line:
                in_regular = True
            if in_regular and "finalPrice" in line:
                # Extract number from line like "  finalPrice     = 95.56"
                parts = line.split("=")
                if len(parts) == 2:
                    try:
                        final_price = float(parts[1].strip())
                    except ValueError:
                        pass
                break
        self.assertIsNotNone(final_price, "Could not parse finalPrice from output.")
        self.assertAlmostEqual(final_price, 95.56, places=1,
            msg=f"Expected ~95.56 but got {final_price}")
        print(f"[PASS] REGULAR no-promo final price = {final_price}")


# ---------------------------------------------------------------------------
# Entry point
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    print("=" * 60)
    print("  Pricing Engine — Integration Test Suite")
    print("=" * 60)
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()
    suite.addTests(loader.loadTestsFromTestCase(TestGradleBuild))
    suite.addTests(loader.loadTestsFromTestCase(TestJUnitSuite))
    suite.addTests(loader.loadTestsFromTestCase(TestBusinessRules))
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    sys.exit(0 if result.wasSuccessful() else 1)
