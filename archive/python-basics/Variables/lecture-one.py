#cost =10
#tax_percet = .25
#tax = cost * tax_percet
#price = cost +tax
#print (price)


#username = "Codingwithteacher"
#first_name = " Michel "
#print (username + "" +first_name)

#first_num = 10
#second_num = 2
#print (first_num)
#print (second_num)

#first_num = 1
#print (first_num)
# print (second_num)

#going to print hello world
#print ("michel")

#this is going over
#multiple lines
#lines

"""""
this is going over
multiple lines
lines
assigment1

wallet = 50

item1 = 15
tax = .03

money_left = wallet - item1 - (item1 * tax)
print ("you have left",money_left)

#string formatting
#if we take away the F string we're no longer allowed to put the variable in the string brackets
print (f"hi {first_name}")
#without the F string 
#print ("hi {}".format(first_name))


sentence = "hi {} {}"
last_name = "lopes"
print (sentence.format(first_name,last_name))

print (f"hi {first_name} {last_name} I hope you are learning")
"""
#user input

"""first_name = input("what is your first name?")
days = input("how many days before your birthday?")
print (f"hi{first_name} only {days}"
       f" before your birthday")"""


#assigment2
#days = input("how many days until your birthday?")
#weeks = int(days) / 7
#print (f"you have {weeks} weeks until your birthday")


#weeks = int(days) / 7
#if weeks == 0:
  #  print(f"you have {weeks} week until your birthday")
    # depois que o usuario coloc input o valor o mesmo de dividido e convertido na int por 7
#else:
   # print("please insert only numbers")
    # se o usuario nao colocar um numero ele vai receber essa mensagem



#assigment3
 #"""List are a collection of data"""

#my_list = [80,96,72,100,8]
#print (my_list)

#people_list = ["michel","jose","maria","joao"]
#print (my_list)

#people_list = ["michel","jose","maria","joao"]
#lens 
#print (len(people_list))


#print (people_list[0:2])
#print (my_list[0:1])

#append functionality ele serve para adicionar um valor na lista
#my_list.append(10000)

"""my_list = [80,96,72,100,8]
print (my_list)
my_list.append(1000)
my_list.insert(2,1000)
print (my_list)
my_list.remove(80)
print (my_list)
my_list.sort()
print (my_list)"""

#Use curly brackets to create a set

"""my_set = {1,2,3,4,5,1,2}
print (my_set)
print (len(my_set))

for x in my_set:
    print (x)

my_set.discard(3)
print (my_set)
my_set.add(6)
print (my_set)
my_set.update([7,8,9])
print (my_set)"""

"""my_tuple = (1,2,3,4,5)
print (my_tuple)
print (my_tuple[1])
my_tuple[1] = 100"""

#creating a list of 5 animals called zoo

my_zoo = ["lion","tiger","elephant","giraffe","monkey"]
print (my_zoo)
#Delete the animal in the 3rd index
my_zoo.pop(3)
print (my_zoo)
#add new animal to the end on the list
my_zoo.append("zebra")
print (my_zoo)
#printing only the first 3 animals
print (my_zoo[0:3])
#delete the animal at the beginner of the list
my_zoo.pop(0)
print (my_zoo)

for x in my_zoo:    
    print (x)
print (my_zoo[0:3])

