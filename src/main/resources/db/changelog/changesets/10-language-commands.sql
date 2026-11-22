--liquibase formatted sql
--changeset itmo-team:10-language-commands

ALTER TABLE language
    ADD COLUMN source_filename VARCHAR(100) NOT NULL DEFAULT 'solution.cpp',
    ADD COLUMN compile_command VARCHAR(500) NULL,
    ADD COLUMN run_command VARCHAR(500) NOT NULL DEFAULT './solution';

UPDATE language SET source_filename = 'Solution.java', compile_command = '/usr/bin/javac -encoding UTF-8 Solution.java', run_command = '/usr/bin/java -Xss64m Solution' WHERE name = 'Java';
UPDATE language SET source_filename = 'Solution.kt', compile_command = '/usr/bin/kotlinc Solution.kt -include-runtime -d solution.jar', run_command = '/usr/bin/java -jar solution.jar' WHERE name = 'Kotlin';
UPDATE language SET source_filename = 'solution.py', compile_command = '/usr/bin/python3 -m py_compile solution.py', run_command = '/usr/bin/python3 solution.py' WHERE name = 'Python';
UPDATE language SET source_filename = 'solution.cpp', compile_command = '/usr/bin/g++ -O3 solution.cpp -o solution', run_command = './solution' WHERE name = 'C++';
UPDATE language SET source_filename = 'solution.go', compile_command = '/usr/bin/go build -o solution solution.go', run_command = './solution' WHERE name = 'Go';
UPDATE language SET source_filename = 'solution.rs', compile_command = '/usr/bin/rustc -O solution.rs -o solution', run_command = './solution' WHERE name = 'Rust';
UPDATE language SET source_filename = 'solution.cs', compile_command = '/usr/bin/dotnet build -c Release -o out', run_command = '/usr/bin/dotnet out/solution.dll' WHERE name = 'C#';
UPDATE language SET source_filename = 'solution.js', compile_command = NULL, run_command = '/usr/bin/node solution.js' WHERE name = 'JavaScript';
UPDATE language SET source_filename = 'solution.ts', compile_command = '/usr/bin/tsc solution.ts', run_command = '/usr/bin/node solution.js' WHERE name = 'TypeScript';
UPDATE language SET source_filename = 'solution.swift', compile_command = '/usr/bin/swiftc -O solution.swift -o solution', run_command = './solution' WHERE name = 'Swift';
