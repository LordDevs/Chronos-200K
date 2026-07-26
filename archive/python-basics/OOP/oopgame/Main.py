from Ogre import *
from zombie import *

def battle(e1: Enemy, e2: Enemy):
    e1.talk()
    e2.talk()

    while e1.health_points > 0 and e2.health_points > 0:
        print('------------------')

        e1.special_attack()
        e2.special_attack()
        print(f'{e1.get_type_of_enemy()}: {e1.health_points} HP left')
        print(f'{e2.get_type_of_enemy()}: {e2.health_points} HP left')

        e2.attack()
        e1.health_points -= e2.attack_damage
        e1.attack()
        e2.health_points -= e1.attack_damage

        print('------------------')

    if e1.health_points > 0:
        print(f'{e1.get_type_of_enemy()} wins!')
    else:
        print(f'{e2.get_type_of_enemy()} wins!')

zombie = Zombie(10, 1)   # HP=10, dano=2
ogre = Ogre(20, 3)       # HP=20, dano=3

#print(f'{zombie.get_type_of_enemy()} has {zombie.health_points} health points and can attack for {zombie.attack_damage}')
#print(f'{ogre.get_type_of_enemy()} has {ogre.health_points} health points and can attack for {ogre.attack_damage}')

battle(zombie, ogre)



#zombie.talk()
#zombie.walk_forward()
#zombie.attack()

#ogre.talk()
#ogre.walk_forward()
#ogre.attack()