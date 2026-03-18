# Scoreboard Assignment (Pure Java)

โปรเจกต์นี้เป็นคำตอบของโจทย์ `scoreboard` โดยใช้ `Java OOP` ล้วน ไม่มี Maven, ไม่มี JUnit, และไม่มี dependency ภายนอก สามารถ compile และ run ได้ด้วย `javac` และ `java` โดยตรง

## สำหรับอาจารย์

เงื่อนไขที่ใช้ตรวจ:

- ใช้ `Java 17`
- ใช้ `doubly linked list` จริง
- เก็บข้อมูลเป็น `ชื่อ + คะแนน`
- รองรับ scoreboard ขนาดไม่เกิน `10` อันดับ
- เรียงคะแนนอัตโนมัติจากมากไปน้อย
- เมื่อมีคะแนนใหม่ จะถูกแทรกในตำแหน่งที่ถูกต้อง
- ถ้าลิสต์เต็ม จะตัดอันดับสุดท้ายออก

## วิธีรันทันที

### macOS / Linux

```bash
mkdir -p out/main out/test
javac -Werror -Xlint:all -encoding UTF-8 -d out/main $(find src -name "*.java")
javac -Werror -Xlint:all -encoding UTF-8 -cp out/main -d out/test $(find test -name "*.java")
java -cp out/main com.scoreboard.app.Main
java -cp out/main:out/test com.scoreboard.test.TestRunner
```

### Windows PowerShell

```powershell
New-Item -ItemType Directory -Force out/main, out/test | Out-Null
$mainSources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName
javac -Werror -Xlint:all -encoding UTF-8 -d out/main $mainSources
$testSources = Get-ChildItem -Recurse -Filter *.java test | ForEach-Object FullName
javac -Werror -Xlint:all -encoding UTF-8 -cp out/main -d out/test $testSources
java -cp out/main com.scoreboard.app.Main
java -cp "out/main;out/test" com.scoreboard.test.TestRunner
```

## ผลลัพธ์ที่ควรได้จากโปรแกรมหลัก

```text
Initial board
Ryu        100
Ken        98
Chunli     95
Sagat      94

After Vega got new score = 97
Ryu        100
Ken        98
Vega       97
Chunli     95

Break-even (singly, 32-bit): 80.00
Break-even (doubly, 64-bit): 50.00
```

## โครงสร้างไฟล์

- `src/com/scoreboard/domain/PlayerScore.java`
- `src/com/scoreboard/domain/Scoreboard.java`
- `src/com/scoreboard/domain/TopScoreboard.java`
- `src/com/scoreboard/domain/BreakEvenCalculator.java`
- `src/com/scoreboard/app/Main.java`
- `test/com/scoreboard/test/Assertions.java`
- `test/com/scoreboard/test/TestRunner.java`
- `test/com/scoreboard/domain/TopScoreboardTest.java`
- `test/com/scoreboard/app/MainTest.java`

## แนวคิดการออกแบบ

- `PlayerScore` เป็น immutable value object
- `TopScoreboard` เป็น implementation หลักของ scoreboard
- ภายใน `TopScoreboard` ใช้ doubly linked list ผ่าน node ที่มี `previous` และ `next`
- ใช้ map ช่วยค้นหาผู้เล่นเดิมเพื่ออัปเดตคะแนนได้ตรงตัว
- `TestRunner` ใช้รันทดสอบทุกกรณีโดยไม่พึ่ง framework ภายนอก

## Break-even

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

ดังนั้น break-even ของ doubly linked list บน 64-bit จะต่ำลงจาก singly linked list บน 32-bit เพราะ node overhead สูงขึ้น
