create table if not exists role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

create table if not exists users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES role(id)
);

create table if not exists teacher (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    patronymic VARCHAR(100),
    phone_number VARCHAR(15),
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

create table if not exists student (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    patronymic VARCHAR(100),
    date_of_birth DATE,
    phone_number VARCHAR(15),
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

create table if not exists module (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

create table if not exists course_group (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    teacher_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    invite_code VARCHAR(12) UNIQUE,
    CONSTRAINT fk_group_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_group_module FOREIGN KEY (module_id) REFERENCES module(id)
);

create table if not exists student_group (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    CONSTRAINT fk_student_group_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_group_group FOREIGN KEY (group_id) REFERENCES course_group(id) ON DELETE CASCADE,
    CONSTRAINT uk_student_group UNIQUE (student_id, group_id)
);

create table if not exists lesson (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    teacher_id BIGINT NOT NULL,
    CONSTRAINT fk_lesson_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

create table if not exists lecture (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    module_id BIGINT NOT NULL,
    CONSTRAINT fk_lecture_module FOREIGN KEY (module_id) REFERENCES module(id)
);

create table if not exists lecture_student (
    id BIGSERIAL PRIMARY KEY,
    lecture_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    progress_percent INTEGER DEFAULT 0,
    CONSTRAINT fk_lecture_student_lecture FOREIGN KEY (lecture_id) REFERENCES lecture(id) ON DELETE CASCADE,
    CONSTRAINT fk_lecture_student_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    CONSTRAINT uk_lecture_student UNIQUE (lecture_id, student_id)
);

create table if not exists lesson_student (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    progress_percent INTEGER DEFAULT 0,
    cards_studied INTEGER DEFAULT 0,
    total_cards INTEGER DEFAULT 0,
    CONSTRAINT fk_lesson_student_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
    CONSTRAINT fk_lesson_student_student FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    CONSTRAINT uk_lesson_student UNIQUE (lesson_id, student_id)
);

create table if not exists flashcard (
    id BIGSERIAL PRIMARY KEY,
    lesson_id BIGINT NOT NULL,
    term VARCHAR(100) NOT NULL,
    definition TEXT NOT NULL,
    example TEXT,
    difficulty INTEGER DEFAULT 1,
    CONSTRAINT fk_flashcard_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE
);