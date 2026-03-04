# Scoreboard (C# / .NET)

โครงงานนี้เป็น baseline สำหรับโจทย์ scoreboard แบบ production-oriented โดยใช้ C# และ .NET เพื่อให้รันได้บน macOS, Linux และ Windows ด้วยมาตรฐานเดียวกัน

## ทำไม C# เหมาะกับงานนี้

- เหมาะกับงาน enterprise: tooling ครบ (`dotnet`, test runner, profiler, CI/CD integration)
- เขียนเชิงโครงสร้างได้ดี: แยก Domain/App/Test ชัดเจน
- cross-platform จริง: runtime เดียวกันบนทุกระบบ
- maintain ระยะยาวง่าย: strong typing, analyzer, test ecosystem ดี

ถ้าทีมคุณมี Java ecosystem หนักมากอยู่แล้ว Java ก็เป็นตัวเลือกที่ดีพอ ๆ กัน แต่ถ้าเริ่มใหม่และต้องการ productivity + readability + cross-platform ที่คงเส้นคงวา C# เป็นตัวเลือกที่เหมาะมาก

## Requirement ที่รองรับ

- score board ขนาดไม่เกิน 10 อันดับ (`TopScoreboard` บังคับ capacity 1..10)
- โครงสร้างข้อมูล: `ชื่อ + คะแนน`
- เรียงลำดับคะแนนอัตโนมัติ (descending)
- เมื่อมี score ใหม่: แทรกตำแหน่งที่ถูกต้อง และตัดอันดับท้ายสุดเมื่อเกินขนาด
- ใช้ **doubly linked list** จริง (มี `Previous` และ `Next` pointer)

## Project Structure

- `src/Scoreboard.Domain`:
  - `TopScoreboard`: business logic หลัก
  - `PlayerScore`: domain value object
  - `BreakEvenCalculator`: สูตร break-even
- `src/Scoreboard.App`:
  - console demo ตามตัวอย่างในโจทย์
- `tests/Scoreboard.Tests`:
  - unit tests ครอบคลุมกรณีสำคัญ

## มาตรฐานที่ตั้งไว้ทั้งระบบ

- `Directory.Build.props`:
  - `Nullable` เปิด
  - `TreatWarningsAsErrors` เปิด
  - `EnforceCodeStyleInBuild` เปิด
  - `AnalysisLevel` ระดับสูง
- `.editorconfig`: style กลางของ repository
- unit tests: บังคับพฤติกรรมหลักของระบบ

## การรัน

```bash
dotnet build
dotnet test
dotnet run --project src/Scoreboard.App
```

## CI Pipeline และ Quality Gate

ไฟล์ pipeline: `.github/workflows/ci.yml`

- `build_and_test`:
  - รันบน `ubuntu-latest`, `windows-latest`, `macos-latest`
  - ทำ `restore -> build (Release) -> test`
- `coverage_quality_gate`:
  - รันบน `ubuntu-latest`
  - quality gate ด้าน style: `dotnet format --verify-no-changes`
  - รัน test พร้อมเก็บ coverage และบังคับ threshold (`line >= 85%`)
  - สร้าง summary + HTML report แล้วเก็บเป็น artifact

ถ้า coverage ต่ำกว่า threshold งานจะ fail ทันที

## Break-even (จากโจทย์)

สูตรฐาน:

```text
DE = n(P + E)
n  = DE / (P + E)
```

- `E` = ขนาดข้อมูลต่อ node
- `P` = ขนาด pointer
- `D` = จำนวน element ใน array

ถ้ารู้ค่า break-even ของ singly linked list บน 32-bit (`n_s32`) แล้วต้องการแปลงเป็น doubly linked list บน 64-bit (`n_d64`):

```text
n_s32 = DE / (E + 4)
n_d64 = DE / (E + 16)
n_d64 = n_s32 * (E + 4) / (E + 16)
```

จึงได้ว่า break-even ของ doubly 64-bit จะต่ำลงจาก singly 32-bit เสมอ (เพราะ node overhead สูงขึ้น)
