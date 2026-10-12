# DifferentiableABS

Experiments in **differentiable, staged programming** for agent-based simulation in Scala: write simulation models as ordinary staged programs, and obtain gradients with respect to their parameters — enabling optimization and learning on top of discrete, stateful, parallel simulations.

## Contents

- `src/main/scala/example/LanternEx.scala` — examples with the Lantern staging API
- `src/main/scala/example/LanternStagedCompiler.scala` — a staged Lantern compiler
- `src/resources/one-dimension/` — dual numbers, expressions, and reverse-mode automatic differentiation over a one-dimensional domain
- `src/test/scala/example/TestSpec.scala` — tests

## Build

sbt project; see `build.sbt`.
