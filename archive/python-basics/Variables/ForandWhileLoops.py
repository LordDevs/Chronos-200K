"""
for & while loops

"""

"""my_list = [1, 2, 3, 4, 5]
print(my_list[0])
print(my_list[1])
print(my_list[2])
print(my_list[3])
print(my_list[4])

sum_of_for_loop = 0
for x in my_list:
    sum_of_for_loop += x
    print(sum_of_for_loop)"""


"""my_list = ["monday, tuesday, wednesday, thursday, friday"]
for x in my_list:
    print(f"Today is {x}")"""

"""i = 0
while i < 5:
    
    i += 1
    if i == 3:
        continue
    print(i)
    if i == 4:
        break
else:
    print("i is now larger or equal to 5")"""

#exercise
#1
#Given: my_list = ["monday", "tuesday", "wednesday", "thursday", "friday"]
#create a while loop that prints all elements of my_list variable 3 times
#when printing the elements, use a for loop to print each character of the element
#however, if the element of the for loop is equal to monday, continue withou prnting

my_list = ["monday", "tuesday", "wednesday", "thursday", "friday"]
i = 0
while i < 3:# while i is less than 3 
    for x in my_list:# for each element in my_list
        i += 1 #increment i by 1 to avoid infinite loop
        if x == "monday":#if x is equal to monday, continue to the next element 
            print("--------")
            continue
        print(x)
    