# Binary search tree drawback

- not balanced
- are all BST leaves interior node?

# Red-black tree

- Red node constraint: both children are black (there are always 2 children)
- Rule : root node is always Black
- my guess: "Black" is priority to fill up
- why both excluding and including?
- from and node
- even it's all black nodes, it can possibly violate the constraint

# process red-black tree

- The way we insert is following the rule of BST

## - red uncle : only change color of nodes

## - no red uncle : 4 cases

### - straight line: change relationship between P and G

### - bended tree: C takes care both P and G (NOTE: check red-uncle rule first because they have collapse case)
