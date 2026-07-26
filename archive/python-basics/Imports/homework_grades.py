"""
-module get used all the time throughout programing

- it helps with creating more files, with unique purposes, to help with clean maintainable code.

"""

import Imports.grade_average_service as grade_service

homework_assignment_grades = {
    'homewok_1': 85,
    'homework_2': 100,
    'homework_3':81
}

grade_service.calculate_homework(
    homework_assignment_grades
)
