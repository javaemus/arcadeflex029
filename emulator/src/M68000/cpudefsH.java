/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package M68000;

/**
 *
 * @author george
 */
public class cpudefsH {
  //pointer for opcode structure
  public static abstract interface opcode_ptr
  {
    public abstract void handler(long opcode);
  }
}
