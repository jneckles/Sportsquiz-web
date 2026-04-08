INSERT INTO `quizzes` (`category`, `title`) VALUES
('basketball', 'NBA Trivia Challenge'),
('soccer', 'World Cup Wonders'),
('football', 'NFL Knowledge Test');

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'Who holds the record for most points scored in a single NBA game?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(1, 'A', 'LeBron James', FALSE),
(1, 'B', 'Wilt Chamberlain', TRUE),
(1, 'C', 'Kobe Bryant', FALSE),
(1, 'D', 'Michael Jordan', FALSE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'Which NBA team has won the most championships (as of early 2020s)?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(2, 'A', 'Los Angeles Lakers', FALSE),
(2, 'B', 'Boston Celtics', TRUE),
(2, 'C', 'Chicago Bulls', FALSE),
(2, 'D', 'Golden State Warriors', FALSE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(1, 'What does "NBA" stand for?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(3, 'A', 'National Ballers Association', FALSE),
(3, 'B', 'National Basketball Affiliation', FALSE),
(3, 'C', 'National Basketball Association', TRUE),
(3, 'D', 'Northern Basketball Alliance', FALSE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(2, 'Which country won the first ever FIFA World Cup in 1930?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(4, 'A', 'Brazil', FALSE),
(4, 'B', 'Argentina', FALSE),
(4, 'C', 'Italy', FALSE),
(4, 'D', 'Uruguay', TRUE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(2, 'Which player has scored the most goals in FIFA World Cup history?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(5, 'A', 'Pelé', FALSE),
(5, 'B', 'Lionel Messi', FALSE),
(5, 'C', 'Miroslav Klose', TRUE),
(5, 'D', 'Ronaldo (Brazilian)', FALSE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(3, 'Which NFL team won the first Super Bowl?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(6, 'A', 'Dallas Cowboys', FALSE),
(6, 'B', 'Kansas City Chiefs', FALSE),
(6, 'C', 'Green Bay Packers', TRUE),
(6, 'D', 'New York Jets', FALSE);

INSERT INTO `questions` (`quiz_id`, `text`) VALUES
(3, 'How many points is a touchdown worth in American Football (before the extra point)?');
INSERT INTO `options` (`question_id`, `option_key`, `option_text`, `is_correct`) VALUES
(7, 'A', '3', FALSE),
(7, 'B', '6', TRUE),
(7, 'C', '7', FALSE),
(7, 'D', '2', FALSE);

