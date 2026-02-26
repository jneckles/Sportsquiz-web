
--  Data for the Sports Quiz Application Database


--  auto_increment IDs start from 1.


-- Populate `quizzes` table

INSERT INTO `quizzes` (`category`, `title`) VALUES
('basketball', 'NBA Trivia Challenge'),   -- Assumes ID=1
('soccer', 'World Cup Wonders'),          -- Assumes ID=2
('football', 'NFL Knowledge Test');       -- Assumes ID=3


-- Populate `questions` and `options` for Basketball Quiz (Quiz ID = 1)

-- Question 1 (Assumes ID=1 after inserts, first question gets ID=1 if table was empty)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'Who holds the record for most points scored in a single NBA game?');
-- Assuming Question ID becomes 1
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(1, 'A', 'LeBron James', FALSE),
(1, 'B', 'Wilt Chamberlain', TRUE), -- Correct answer
(1, 'C', 'Kobe Bryant', FALSE),
(1, 'D', 'Michael Jordan', FALSE);

-- Question 2 (Assumes ID=2)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'Which NBA team has won the most championships (as of early 2020s)?');
-- Assuming Question ID becomes 2
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(2, 'A', 'Los Angeles Lakers', FALSE),
(2, 'B', 'Boston Celtics', TRUE),   -- Correct
(2, 'C', 'Chicago Bulls', FALSE),
(2, 'D', 'Golden State Warriors', FALSE);

-- Question 3 (Assumes ID=3)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'What does "NBA" stand for?');
-- Assuming Question ID becomes 3
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(3, 'A', 'National Ballers Association', FALSE),
(3, 'B', 'National Basketball Affiliation', FALSE),
(3, 'C', 'National Basketball Association', TRUE), -- Correct
(3, 'D', 'Northern Basketball Alliance', FALSE);


-- Populate `questions` and `options` for Soccer Quiz (Quiz ID = 2)

-- Question 4 (Assumes ID=4)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(2, 'Which country won the first ever FIFA World Cup in 1930?');
-- Assuming Question ID becomes 4
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(4, 'A', 'Brazil', FALSE),
(4, 'B', 'Argentina', FALSE),
(4, 'C', 'Italy', FALSE),
(4, 'D', 'Uruguay', TRUE); -- Correct

-- Question 5 (Assumes ID=5)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(2, 'Which player has scored the most goals in FIFA World Cup history?');
-- Assuming Question ID becomes 5
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(5, 'A', 'Pelé', FALSE),
(5, 'B', 'Lionel Messi', FALSE),
(5, 'C', 'Miroslav Klose', TRUE), -- Correct
(5, 'D', 'Ronaldo (Brazilian)', FALSE);

-- Populate `questions` and `options` for Football Quiz (Quiz ID = 3)

-- Question 6 (Assumes ID=6)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(3, 'Which NFL team won the first Super Bowl?');
-- Assuming Question ID becomes 6
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(6, 'A', 'Dallas Cowboys', FALSE),
(6, 'B', 'Kansas City Chiefs', FALSE),
(6, 'C', 'Green Bay Packers', TRUE), -- Correct (Super Bowl I)
(6, 'D', 'New York Jets', FALSE);

-- Question 7 (Assumes ID=7)
INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(3, 'How many points is a touchdown worth in American Football (before the extra point)?');
-- Assuming Question ID becomes 7
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(7, 'A', '3', FALSE),
(7, 'B', '6', TRUE), -- Correct
(7, 'C', '7', FALSE),
(7, 'D', '2', FALSE);


