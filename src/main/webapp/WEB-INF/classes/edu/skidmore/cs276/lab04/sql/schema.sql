
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS 'options';
DROP TABLE IF EXISTS 'questions';
DROP TABLE IF EXISTS 'quizzes';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS=1;

-- -----------------------------------------------------
-- Table `quizzes`
-- Stores information about each quiz category.
-- -----------------------------------------------------
CREATE TABLE `quizzes` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Auto-incrementing primary key',
  `category` VARCHAR(100) NOT NULL COMMENT 'Unique category name used internally and for selection (e.g., \'basketball\', \'soccer\')',
  `title` VARCHAR(255) NOT NULL COMMENT 'User-friendly display title for the quiz',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_category` (`category` ASC) VISIBLE
  -- Use ASC and VISIBLE for clarity, often default in newer MySQL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores information about each quiz category.';


-- -----------------------------------------------------
-- Table `questions`
-- Stores the individual questions for each quiz.
-- -----------------------------------------------------
CREATE TABLE `questions` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Auto-incrementing primary key',
  `quiz_id` INT NOT NULL COMMENT 'References the quiz this question belongs to.',
  `text` TEXT NOT NULL COMMENT 'The text content of the quiz question. Using TEXT for potentially longer questions.',
  PRIMARY KEY (`id`),
  INDEX `fk_questions_quizzes_idx` (`quiz_id` ASC) VISIBLE, -- Index for the foreign key
  CONSTRAINT `fk_questions_quizzes`
    FOREIGN KEY (`quiz_id`)
    REFERENCES `quizzes` (`id`)
    ON DELETE CASCADE -- If a quiz is deleted, delete its questions too
    ON UPDATE NO ACTION -- Default behavior, can be explicit
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores the individual questions for each quiz.';


-- -----------------------------------------------------
-- Table `options`
-- Stores the multiple-choice options for each question.
-- -----------------------------------------------------
CREATE TABLE `options` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Auto-incrementing primary key',
  `question_id` INT NOT NULL COMMENT 'References the question this option belongs to.',
  `option_key` VARCHAR(5) NOT NULL COMMENT 'Identifier for the option within its question (e.g., A, B, C).',
  `option_text` VARCHAR(500) NOT NULL COMMENT 'The display text for the multiple-choice option.',
  `is_correct` BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'TRUE (1) if this option is the correct answer, FALSE (0) otherwise.',
  PRIMARY KEY (`id`),
  INDEX `fk_options_questions_idx` (`question_id` ASC) VISIBLE, -- Index for the foreign key
  UNIQUE INDEX `uq_question_option_key` (`question_id` ASC, `option_key` ASC) VISIBLE, -- Unique combination of question and option key
  CONSTRAINT `fk_options_questions`
    FOREIGN KEY (`question_id`)
    REFERENCES `questions` (`id`)
    ON DELETE CASCADE -- If a question is deleted, delete its options too
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores the multiple-choice options for each question.';
