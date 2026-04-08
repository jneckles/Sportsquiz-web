CREATE TABLE IF NOT EXISTS `quizzes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(100) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uq_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `questions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `quiz_id` INT NOT NULL,
  `text` TEXT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_questions_quizzes_idx` (`quiz_id`),
  CONSTRAINT `fk_questions_quizzes`
    FOREIGN KEY (`quiz_id`)
    REFERENCES `quizzes` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `options` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `question_id` INT NOT NULL,
  `option_key` VARCHAR(5) NOT NULL,
  `option_text` VARCHAR(500) NOT NULL,
  `is_correct` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  INDEX `fk_options_questions_idx` (`question_id`),
  UNIQUE INDEX `uq_question_option_key` (`question_id`, `option_key`),
  CONSTRAINT `fk_options_questions`
    FOREIGN KEY (`question_id`)
    REFERENCES `questions` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
