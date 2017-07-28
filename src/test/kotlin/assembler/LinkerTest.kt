package venus.linker

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import venus.simulator.Simulator
import venus.assembler.Assembler
import venus.assembler.AssemblerError

class LinkerTest {
    @Test
    fun linkOneFile() {
        val prog = Assembler.assemble("""
        start:
        addi x8 x8 1
        addi x9 x0 2
        beq x8 x9 skip
        jal x0 start
        skip:
        """)
        val linked = Linker.link(listOf(prog))
        var sim = Simulator(linked)
        sim.run()
        assertEquals(2, sim.getReg(8))
    }

    @Test
    fun linkTwoFiles() {
        val prog1 = Assembler.assemble("""
        foo:
            jal x0 bar
            addi x8 x0 8
        .globl foo
        """)
        val prog2 = Assembler.assemble("""
        .globl bar
        bar:
            addi x8 x8 1
        """)
        val linked = Linker.link(listOf(prog1, prog2))
        var sim = Simulator(linked)
        sim.run()
        assertEquals(1, sim.getReg(8))
    }

    @Test
    fun privateLabel() {
        val prog1 = Assembler.assemble("""
        foo:
            jal x0 _bar
            addi x8 x0 8
        .globl foo
        """)
        val prog2 = Assembler.assemble("""
        _bar:
            addi x8 x8 1
        """)

        try {
            Linker.link(listOf(prog1, prog2))
            fail("allowed jump to 'private' label")
        } catch (e: AssemblerError) {
            assertTrue(true)
        }
    }

    @Test
    fun loadAddress() {
        val prog = Assembler.assemble("""
        .data
        magic: .byte 42
        .text
        la x8 magic
        lb x9 0(x8)
        """)
        val linked = Linker.link(listOf(prog))
        val sim = Simulator(linked)
        sim.run()
        assertEquals(42, sim.getReg(9))
    }

    @Test
    fun loadAddressBefore() {
        val prog = Assembler.assemble("""
        .data
        padder:
        .asciiz "padpadpad"
        magic:
        .byte 42
        .text
        nop
        nop
        nop
        nop
        la x8 magic
        lb x9 0(x8)
        """)
        val linked = Linker.link(listOf(prog))
        val sim = Simulator(linked)
        sim.run()
        assertEquals(42, sim.getReg(9))
    }
}
