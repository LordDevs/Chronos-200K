"""
Dictionaries
"""
# Dictionaries are used to store data values in key:value pairs.
# A dictionary is a collection which is unordered, changeable and does not allow duplicates.

"""user_dictionary = {
    'username': 'michel',
    'name': 'Michel souza',
    'age': 34,
}"""
#user_dictionary["married"] = True
#print(user_dictionary.get('username'))# get the value of the key username
#print(len(user_dictionary))# get the length of the dictionary

#user_dictionary.pop('age')# remove the key age from the dictionary
#print(user_dictionary)

#user_dictionary.clear()# clear the dictionary
#print(user_dictionary)

#del user_dictionary# delete the dictionary

#for x in user_dictionary: # loop through the dictionary
   # print(x) # print the keys of the dictionary

#for x, y in user_dictionary.items(): #
    #print(x, y) # print the key:value pairs of the dictionary

"""user_dictionary2 = user_dictionary.copy()
user_dictionary2.pop('age')
print(user_dictionary2)"""

#exercise 2
#create a for loop to print all keys and values
#create a new variable vehicle2, which is a copy of my_vehicle
#add a new key 'number_of_tires' to th vehicle 2 variable that is equal to 4
#delete the mileage key and value from vehicle2
#print just the keys from vehicle2


my_vehicle = {# create a dictionary
    'brand': 'ford',
    'model': 'focus',
    'year': 2019,
    'mileage': 30000
}

vehicle2 = my_vehicle.copy()# copy the dictionary
for x, y in my_vehicle.items():# loop through the dictionary
    print(x, y)# print the key:value pairs of the dictionary

vehicle2['number_of_tires'] = 4# add a new key:value pair to the dictionary
vehicle2.pop('mileage')# remove the key:value pair from the dictionary
print(vehicle2)#wrong

#correction of line 55
for x in vehicle2:# loop through the dictionary
    print(x)# print the keys of the dictionary




