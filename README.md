# CS1501 Assignment 5


## Goal:
To get hands on experience with algorithms to perform mathematical operations
on large integers.


## High-level description:
You will be writing a replacement for Java's `BigInteger` to perform
multiplications and to run the extended Euclidean algorithm on integers values
that would overflow `long`.


## Specifications:
1. You are provided with the start of a class to process arbitrarily sized
	integers called `HeftyInteger`. `HeftyInteger`s are represented internally
	as [two's-complement](https://en.wikipedia.org/wiki/Two%27s_complement)
	_raw integers_ using byte arrays (i.e., instances of `byte[]`).

	1. Currently, `HeftyInteger` has the following operations implemented:

		* A constructor that creates a new `HeftyInteger` based off of a
			provided `byte[]`.
		*  A method to compute the sum of two `HeftyInteger`s.
		*  A method to determine the negation of a `HeftyInteger`.
		*  A method to compute the difference of two `HeftyInteger`s.
		*  Several other helper methods.

	1. Due to the use of a two's complement representation of the integers,
		positive `HeftyInteger`s should always have at least one leading 0 bit
		(indicating that the integer is positive) in their `byte[]`
		representation.  This property may cause the array to be bigger than
		expected (e.g., a 1024-bit positive integer will be represented using a
		length 129 byte array).

	1. `HeftyInteger`s are also be represented using a _big-endian_ byte-order,
		so the most significant byte is at the 0<sup>th</sup> index of the
		`byte[]`.

	1.  You will need to implement the following functions:
		*  `HeftyInteger multiply(HeftyInteger other)`
		*  `HeftyInteger[] XGCD(HeftyInteger other)`
		*  Any additional helper functions that you deem necessary.

	1. You may *not* use any calls the Java API class `java.math.BigInteger`,
		or any other JCL class within `HeftyInteger`.

1. As always, there are several test cases provided. They use `BigInteger` to:
	
	* Generate `byte[]` representations of integers from `String`s containing
		base-10 integers

	* Verify the results of your `HeftyInteger` methods

1. To get full credit, your implementation should be efficient enough to
	complete multiplication or XGCD given 200-digit inputs within 3 minutes.


## Submission Guidelines:
* **DO NOT** add `./app/build/` to your git repository.

* Be sure to remember to push the latest copy of your code back to your GitHub
	repository before submitting. To submit, log into GradeScope from Canvas and
	have GradeScope pull your repository from GitHub.


## Grading Rubric
| Feature | Points
| ------- | ------:
| `HeftyInteger.multiply()` works properly | 40
| `HeftyInteger.XGCD()` works properly | 54
| Assignment info sheet/submission | 6
