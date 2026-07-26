import random


class Enemy:
    def __init__(self, type_of_enemy, health_points, attack_damage):
        self._type_of_enemy = type_of_enemy  # um underline: “protegido”, acessível em subclasses
        self.health_points = health_points
        self.attack_damage = attack_damage

        # Getter explícito (combina com teu Main)
    def get_type_of_enemy(self):
        return self._type_of_enemy

    def talk(self):
        print(f'I am a {self._type_of_enemy}. Be prepared to fight.')

    def walk_forward(self):
        print(f'{self._type_of_enemy} walking forward.')

    def attack(self):
        print(f'{self._type_of_enemy} attacks for {self.attack_damage} damage!')

    def special_attack(self):
        print('Enemy has no special attack')

