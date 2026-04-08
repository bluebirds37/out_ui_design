import { chromium } from "playwright";
import path from "node:path";
import { fileURLToPath } from "node:url";
import fs from "node:fs/promises";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const screens = [
  "welcome",
  "login",
  "register",
  "forgot_password",
  "complete_profile",
  "discover_home",
  "search",
  "map_results",
  "route_detail",
  "waypoint_detail",
  "author_profile",
  "comments",
  "record_prepare",
  "record_live",
  "camera_capture",
  "add_waypoint",
  "route_edit",
  "publish_preview",
  "publish_success",
  "profile",
  "my_routes",
  "favorites",
  "settings",
  "ds_foundations",
  "ds_components",
  "ds_states",
  "ds_empty_loading_errors",
  "ds_recording_exceptions",
  "ds_motion_spec",
  "ds_accessibility_spec",
  "ds_component_state_matrix",
  "ds_sizing_annotations",
  "ds_token_mapping"
];

const outputDir = path.join(__dirname, "export");
const baseUrl = "http://127.0.0.1:4173/ui/src/design.html";

await fs.mkdir(outputDir, { recursive: true });

const browser = await chromium.launch({ headless: true });
const page = await browser.newPage({
  viewport: { width: 1600, height: 1000 },
  deviceScaleFactor: 2
});

for (const screen of screens) {
  await page.goto(`${baseUrl}?screen=${screen}`, { waitUntil: "networkidle" });
  await page.screenshot({
    path: path.join(outputDir, `${screen}.png`)
  });
}

await browser.close();
