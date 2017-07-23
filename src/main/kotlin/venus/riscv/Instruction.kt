package venus.riscv

/**
 * Represents the machine code of an instruction.
 *
 * For now this only supports RV32.
 *
 * @param encoding the underlying machine code of the instruction
 */
class Instruction(private var encoding: Int) {
    val length = 4

    /**
     * Returns the value of the given instruction field for this instruction.
     *
     * @param ifield the instruction field to get
     * @return the value of the given instruction field
     */
    fun getField(ifield: InstructionField): Int {
        return (encoding and ifield.mask) ushr numberOfTrailingZeros(ifield.mask)
    }

    /**
     * Sets an instruction field to the given value.
     *
     * @param ifield the instruction field to set
     * @param value the value to set the field to
     */
    fun setField(ifield: InstructionField, value: Int) {
        encoding = encoding and ifield.mask.inv()
        encoding = encoding or ((value shl numberOfTrailingZeros(ifield.mask)) and ifield.mask)
    }

    /**
     * Finds the number of trailing zeros of a given integer
     *
     * @param n
     * @return number of trailing zeros in input
     */
    private fun numberOfTrailingZeros(n: Int): Int {
        var mask = 1
        for (i in 0 until 32) {
            if (n and mask != 0) return i
            mask = mask shl 1
        }
        return 32
    }
}