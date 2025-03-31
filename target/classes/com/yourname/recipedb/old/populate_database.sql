-- Insert Users
INSERT INTO Users (user_id, username) VALUES (1, 'quinton');
INSERT INTO Users (user_id, username) VALUES (2, 'alex');
INSERT INTO Users (user_id, username) VALUES (3, 'samantha');

-- Insert Ingredients (universal list)
INSERT INTO Ingredients (ingredient_id, name) VALUES (1, 'Flour');
INSERT INTO Ingredients (ingredient_id, name) VALUES (2, 'Sugar');
INSERT INTO Ingredients (ingredient_id, name) VALUES (3, 'Salt');
INSERT INTO Ingredients (ingredient_id, name) VALUES (4, 'Eggs');
INSERT INTO Ingredients (ingredient_id, name) VALUES (5, 'Milk');
INSERT INTO Ingredients (ingredient_id, name) VALUES (6, 'Butter');
INSERT INTO Ingredients (ingredient_id, name) VALUES (7, 'Baking Powder');
INSERT INTO Ingredients (ingredient_id, name) VALUES (8, 'Chicken Breast');
INSERT INTO Ingredients (ingredient_id, name) VALUES (9, 'Olive Oil');
INSERT INTO Ingredients (ingredient_id, name) VALUES (10, 'Garlic');
INSERT INTO Ingredients (ingredient_id, name) VALUES (11, 'Onion');
INSERT INTO Ingredients (ingredient_id, name) VALUES (12, 'Tomato');
INSERT INTO Ingredients (ingredient_id, name) VALUES (13, 'Pasta');
INSERT INTO Ingredients (ingredient_id, name) VALUES (14, 'Cheese');
INSERT INTO Ingredients (ingredient_id, name) VALUES (15, 'Ground Beef');
INSERT INTO Ingredients (ingredient_id, name) VALUES (16, 'Lettuce');
INSERT INTO Ingredients (ingredient_id, name) VALUES (17, 'Rice');
INSERT INTO Ingredients (ingredient_id, name) VALUES (18, 'Soy Sauce');
INSERT INTO Ingredients (ingredient_id, name) VALUES (19, 'Carrots');
INSERT INTO Ingredients (ingredient_id, name) VALUES (20, 'Potatoes');

-- Assign Ingredients to Users (User Storage)
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 1, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 2, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 4, 6);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 5, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 8, 3);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 10, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 12, 3);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 14, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 17, 5);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (1, 18, 2);

INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 3, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 4, 4);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 6, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 7, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 9, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 11, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 13, 3);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 15, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 19, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (2, 20, 4);

INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 2, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 5, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 7, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 10, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 11, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 13, 2);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 14, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 16, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 18, 1);
INSERT INTO User_Ingredients (user_id, ingredient_id, quantity) VALUES (3, 20, 2);

-- Insert Recipes
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (1, 'Pancakes', 'Mix all ingredients and cook on a skillet.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (2, 'Scrambled Eggs', 'Whisk eggs and cook with butter.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (3, 'Grilled Chicken', 'Marinate chicken and grill it.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (4, 'Pasta with Tomato Sauce', 'Boil pasta, make sauce, mix.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (5, 'Fried Rice', 'Cook rice, stir-fry with vegetables and soy sauce.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (6, 'Cheeseburger', 'Cook beef patty, add cheese, serve on bun.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (7, 'Mashed Potatoes', 'Boil potatoes, mash with butter.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (8, 'Garlic Bread', 'Spread garlic butter on bread and bake.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (9, 'Omelette', 'Whisk eggs, cook with fillings.');
INSERT INTO Recipes (recipe_id, name, instructions) VALUES (10, 'Tacos', 'Prepare filling, serve in tortillas.');

-- Associate Ingredients with Recipes
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (1, 1, 2);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (1, 2, 1);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (1, 4, 2);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (1, 5, 1);

INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (2, 4, 3);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (2, 6, 1);

INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (3, 8, 1);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (3, 9, 1);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (3, 10, 1);

INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (4, 13, 2);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (4, 12, 2);
INSERT INTO Recipe_Ingredients (recipe_id, ingredient_id, quantity) VALUES (4, 10, 1);

-- Add more ingredients for other recipes as needed...

COMMIT;
