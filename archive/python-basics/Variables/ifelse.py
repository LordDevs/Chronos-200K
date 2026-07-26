"""flow control with if-elif-else"""


"""x = 27

if x >= 3:
    print("x is greater than ")
else:
    print("x is less than 1")

print("outside of if statement")"""


"""hour = 21

if hour < 15:
    print("Good morning")
elif hour < 20: # elif = else if
    print("Good afternoon")
else:
    print("Good evening")"""

grade = 61-2

if grade >= 90:
            print("A")
elif grade >= 80:
            print("B")
elif grade >= 70:
            print("C")
elif grade >= 60:
            print("D")
else:
            print("F")

#correction (both are correct)

grade = 81

if grade >= 90:
            print("A")
elif 80 <= grade < 90:
            print("B")
elif 70 <= grade < 80:
            print("C")
elif 60 <= grade < 70:
            print("D")
else:
            print("F")