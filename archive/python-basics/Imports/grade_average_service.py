

def calculate_homework(homework_assignments_arg):
    sum_of_grades = 0
    for homework in homework_assignments_arg.values():
        sum_of_grades += homework
    final_grade = round(sum_of_grades /
                        len(homework_assignments_arg) ,5)#round means the decimals we want so I put 2
    print(final_grade)


#this codes means the calculations of all homeworks and divide per 3 and at line 19 we are chosing how many decimals we can choose