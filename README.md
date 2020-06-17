Contains the Eclipse Build Tree for Stephen's Santorini AI.

To run, open the command prompt in the root directory and type "java -cp bin RunSantorini" (without the quotation marks).

The human player is Grey, and moves first. The AI player has been given a timeout for its iterative deepening algorithm: if a particular
depth of the algorithm takes more than 3 seconds, the AI stops looking. That allows some delayed moves, though: if the AI requires
two seconds at depth 4, it'll explore to depth 5, which will probably require closer to thirty seconds.

The game closes automatically upon completion. 
