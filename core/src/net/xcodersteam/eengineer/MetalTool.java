package net.xcodersteam.eengineer;

import net.xcodersteam.eengineer.components.Metal;

/**
 * Created by fantasyday on 28.04.2015.
 */
public class MetalTool extends LineTool {
    @Override
    public boolean perform(Cell cell) {
        if(cell.layers[2] == null)
            new Metal(cell);
        else
            return false;
        return true;
    }

    @Override
    public boolean isLineAble(Cell cell1, Cell cell2) {
        return cell1 != null && cell2!= null && cell1.layers[2] != null;
    }

    @Override
    public void setConnection(byte b, Cell c) {
        c.layers[2].connection = b;
    }

    @Override
    public byte getConnection(Cell c) {
        return c.layers[2].connection;
    }
}