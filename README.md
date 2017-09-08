# FSModel
The model generator generates random models and provides the ability to perturb them at various degrees (e.g., add new paths, remove existing paths).

The method *perturb* specifies the percentage of perturbation required.

**Execution:**

- experiment.txt: contains the initial model
- paths.txt: contains the list of paths in the model
- extrapaths.txt: contains the set of paths to add
- perturbation: the percentage of perturbation in percentage (e.g., 10, 20)
- result.txt: to store the perturbed models


```
$ java -jar FSM.jar experiment.txt paths.txt extrapaths.txt perturbation result.txt

```