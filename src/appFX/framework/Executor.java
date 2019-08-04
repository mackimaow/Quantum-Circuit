package appFX.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.CircuitBoardModel;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;

public class Executor {

    /**
     * Executes quil code by writing to a QVM instance and collecting the output
     * @param quil The quil code to be executed.
     * @return The output of the QVM when given the quil code
     */
    static String execute(String quil) {
        //runs qvm -e
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec("qvm -e");
            BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.getOutputStream().write(quil.getBytes());
            p.getOutputStream().close();


            output = isr.lines().map(l -> l + "\n").reduce(String::concat).get();
            System.out.println("Printing output:");
            System.out.println(output);
            System.out.println("Done printing output");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }



    /**
     * Executes the current project without relying on any external dependencies such as the QVM or a python install.
     * @param p The project to execute
     * @return A string of the resulting wavefunction after execution
     */
    static String executeInternal(Project p) {
        Stream<ExportedGate> exps = null;
        try {
            exps = GateManager.exportGates(p);
            System.out.println("Gate stream created");
        } catch (GateManager.ExportException e) {
        	e.showExportErrorSource();
            return "";
        }
        CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(p.getTopLevelCircuitLocationString());
        int colHeight = cb.getNumberOfRegisters();
        System.out.println(colHeight);
        ArrayList<Matrix<Complex>> columns = new ArrayList<>();
        addCols: for(Iterator<ExportedGate> itr = exps.iterator(); itr.hasNext();) {
            ArrayList<ExportedGate> column = new ArrayList<>();
            for(int i = 0; i < colHeight;) {
                if(!itr.hasNext()) {
                  break addCols;
                }
                ExportedGate eg = itr.next();
                if(eg.getGateType().equals(BasicGateModel.BasicGateModelType.POVM))
                    return executeMixedState(p);
                i += eg.getGateRegister().length;
                column.add(eg);
            }
            columns.add(buildColumnMatrix(column,colHeight));
        } //Columns built
        Matrix<Complex> in = getInVector(colHeight);
        System.out.println("Beginning input vector multiplication");
        for(Matrix<Complex> m : columns) {
            System.out.println(m);
            System.out.println(m.getColumns());
            System.out.println(in);
            in = m.mult(in);
        }
        return in.toString();
    }

    static Matrix<Complex> getInVector(int numregs) {
        Matrix<Complex> input = new Matrix<Complex>(Complex.ZERO(),1<<numregs,1);
        input.r(Complex.ONE(),0,0);
        for(int i = 1; i < numregs; ++i) {
            input.r(Complex.ZERO(),i,0);
        }
        System.out.println("Input vector: ");
        System.out.println(input);
        return input;
    }

    /**
     * Assumes a pure-quantum state and builds the resulting matrix for a column of gates in the circuit
     * @param column An arraylist of exportables representing a column in the circuit
     * @return The matrix of the column
     */
   static Matrix<Complex> buildColumnMatrix(ArrayList<ExportedGate> column, int colheight) {
       //Assumes no overlapping circuit components
       //Perhaps place swap gates to ensure this automatically?
       Matrix<Complex> mat = null;
       Matrix<Complex> colmat = null;
       Matrix<Complex> swapBuffer = Matrix.identity(Complex.ZERO(),1<<colheight);
       int itr = 0; //itr is the gate we are processing, i is the register we are processing
       for (int i = 0; i < colheight; itr++) {
           ExportedGate eg = column.get(itr);
           colmat = eg.getInputMatrixes()[0];
           int span = 1+getMaxElement(eg.getGateRegister())-getMinElement(eg.getGateRegister());
           if (eg.getQuantumControls().length != 0) {

           }
           //Shuffle eg to be contiguous and in order, then pad with identity
           /*
           So if there is a gate with registers
           3
           1
           -
           2
           It becomes
           1
           2
           3
           -
           With a swap buffer
            */
           if (eg.getGateRegister().length != 1) {
               swapBuffer = swapBuffer.mult(getSwapMat(eg.getGateRegister(),colheight));
               System.out.println("buildColumnMatrix: swapBuffer is of size " + swapBuffer.getRows());
               System.out.println(swapBuffer);
               Matrix<Complex> adjustedColmat = colmat.kronecker(Matrix.identity(Complex.ZERO(),1<<(span-eg.getGateRegister().length)));
               System.out.println("buildColumnMatrix: Adjusted colmat is of size " + adjustedColmat.getRows());
               System.out.println(adjustedColmat);
               colmat = adjustedColmat;
           }
           if(mat == null) {
               mat = colmat;
           } else {
               mat = mat.kronecker(colmat);
           }
           i += span;
           itr += span-eg.getGateRegister().length;
       }
       System.out.println("Column matrix size: " + mat.getRows());
       System.out.println("Swap buffer size: " + swapBuffer.getRows());
       return swapBuffer.mult(mat).mult(swapBuffer.transpose());
   }

    private static int getMaxElement(int[] arr) {
        if(arr.length==0) {
            return -1;
        }
        int max = arr[0];
        for(int i = 1; i < arr.length; ++i) {
            if(max < arr[i]) {
                max = arr[i];
            }
        }
        return max;
    }
    private static int getMinElement(int[] arr) {
       if(arr.length==0){
           return -1;
       }
       int min = arr[0];
       for(int i = 1; i < arr.length; ++i) {
           if(min > arr[i]) {
               min = arr[i];
           }
       }
       return min;
    }


    private static Matrix<Complex> getSwapMat(int[] regs, int columnHeight) {
        int len = regs.length;
        Matrix<Complex> swapMat = Matrix.identity(Complex.ZERO(),4);
        swapMat.r(Complex.ZERO(),1,1);
        swapMat.r(Complex.ZERO(),2,2);
        swapMat.r(Complex.ONE(),2,1);
        swapMat.r(Complex.ONE(),1,2);
        //System.out.println("Swap Matrix: " + swapMat.toString());
        Matrix<Complex> buffer = Matrix.identity(Complex.ZERO(),1<<columnHeight);
        /*
        Algorithm: Build swap buffer by bubble-sort like process
        Bring 1st register to top, then second register to second place, etc.
         */
        for(int i = 0; i < len; ++i) {
           int ri = regs[i];
           Matrix<Complex> sc = farSwap(i,ri,columnHeight);
           buffer = buffer.mult(sc);
        }
        return buffer;
    }




    private static Matrix<Complex> farSwap(int p1, int p2, int columnHeight) {
        Matrix<Complex> swapMat = Matrix.identity(Complex.ZERO(),4);
        swapMat.r(Complex.ZERO(),1,1);
        swapMat.r(Complex.ZERO(),2,2);
        swapMat.r(Complex.ONE(),2,1);
        swapMat.r(Complex.ONE(),1,2);

        Matrix<Complex> farSwap = Matrix.identity(Complex.ZERO(),1<<columnHeight);
        if(p1 == p2) {
            return farSwap;
        }
        if(p1 < p2) {
            for(int i = 0; i < p2-p1; ++i) {
                Matrix<Complex> nextLink = identityPad(swapMat,p1+i,columnHeight);
                farSwap = farSwap.mult(nextLink);
            }
            for(int i = 1; i < p2-p1; ++i) {
                Matrix<Complex> nextLink = identityPad(swapMat,p2-i,columnHeight);
                farSwap = farSwap.mult(nextLink);
            }
            return farSwap;
        } else {
             return farSwap(p2,p1,columnHeight);
        }
    }

    /**
     * Takes a gate and returns the matrix for a column consisting of only that gate
     * A call to identityPad with SWAP at 1 and a size of 4 should return
     * ID
     * SWAP1
     * SWAP2
     * ID
     * @param gate The matrix of the gate to be used in the column
     * @param position Where the gate should be in the column; zero indexed
     * @param columnHeight The size of the column
     * @return A matrix representing a column of gates containing identity and the one gate given to the function
     */
    private static Matrix<Complex> identityPad(Matrix<Complex> gate, int position, int columnHeight) {
        int numberOfQubits = 0;
        while(gate.getRows()>>++numberOfQubits > 1);
        Matrix<Complex> column = Matrix.identity(Complex.ZERO(),1);
        for(int i = 0; i < position; ++i) {
            column = column.kronecker(Matrix.identity(Complex.ZERO(),2));
        }
        column = column.kronecker(gate);
        for(int i = position+numberOfQubits; i < columnHeight; ++i) {
            column = column.kronecker(Matrix.identity(Complex.ZERO(),2));
        }
        return column;
    }

    public static String executeMixedState(Project p) {
        CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(p.getTopLevelCircuitLocationString());
        int colHeight = cb.getNumberOfRegisters();
        System.out.println(colHeight);

        //A state |x> is now |x><x|, and applying an operator A|x> is A|x><x|A*
        //Be sure to normalize by dividing by Tr(A |x><x| A*)

        Vector<Complex> zero = new Vector<Complex>(Complex.ONE(),Complex.ZERO());
        Vector<Complex> inputTemp = zero;
        for(int i = 1; i < colHeight; ++i) {
            inputTemp = inputTemp.kronecker(zero).toVector();
        }
        Matrix<Complex> input = inputTemp.outerProduct(inputTemp);

        System.out.println(input);

        //(A tensor B)* = A* tensor B*
        //Therefore (A tensor B)|x> becomes (A tensor B)|x><x|(A* tensor B*)

        ArrayList<Matrix<Complex>> columns = new ArrayList<>();
        //The strategy remains similar. We will build a column, compute the new state, and loop doing that until we're out of circuit
        //Let's construct the gate stream
        Stream<ExportedGate> gateStream = null;
        try {
            gateStream = GateManager.exportGates(p);
        } catch (GateManager.ExportException e) {
            e.showExportErrorSource();
            return "";
        }
        Iterator<ExportedGate> itr = gateStream.iterator();

        //System.out.println("Matrices:");
        //printArray(itr.next().getInputMatrixes());
        //System.out.println("End Matrices");

        ArrayList<ExportedGate> currentColumn = new ArrayList<>();
        //Loop to grab one column of gate
        while(itr.hasNext()) {
            int columnSpaceTakenUp = 0;
            while (columnSpaceTakenUp < colHeight) {
                ExportedGate eg = itr.next();
                int span = 1 + getMaxElement(eg.getGateRegister()) - getMinElement(eg.getGateRegister());
                columnSpaceTakenUp += span;
                currentColumn.add(eg);
            }

            printColumn(currentColumn);

            ArrayList<Matrix<Complex>> columnMatrix = buildColumnDensityMatrix(currentColumn, colHeight);
            input = applyKrausDontLook(columnMatrix,input);
            currentColumn = new ArrayList<>();
        }

        return input.toString();
    }

    private static <T> void printArray(T[] a) {
        for(T t : a) {
            System.out.println(t);
        }
    }

    private static void printColumn(ArrayList<ExportedGate> column) {
        String toPrint = "";
        for(ExportedGate eg : column) {
            if(eg.getGateModel().getName().equalsIgnoreCase("Identity")) {
                toPrint += "Identity\n";
            } else {
                toPrint += eg.getGateType().toString() + "\n";
                if(eg.getGateType().equals(BasicGateModel.BasicGateModelType.POVM))
                    printArray(eg.getInputMatrixes());
            }
        }
        System.out.println(toPrint);
    }



    /**
     * Takes a list of gates in a column, allowing any to be POVMs, and returns the list of kraus matrices defining the action of this column on the state
     * Iff none of the gates are POVMs, the function will return one matrix representing the linear map on density matrices
     * @param egs The exported gates in the column
     * @param columnHeight The size of the column in registers
     * @return The kraus matrices defining the action of this column on the state
     */
    private static ArrayList<Matrix<Complex>> buildColumnDensityMatrix(ArrayList<ExportedGate> egs, int columnHeight) {

        //Tensor Product of two Operators
        //O1 = {A1,A2,...,Ar}
        //O2 = {B1,B2,...,Br}
        //O1 tensor O2 = {Ai tensor Bj | i,j < r}

        //For now, assume all multiqubit structures are contiguous and in order

        egs = removeIdentityUnderGates(egs);

        ArrayList<Matrix<Complex>> kraus = new ArrayList<>();
        Matrix<Complex> column = Matrix.identity(Complex.ZERO(),1);
        kraus.add(column);
        while(!egs.isEmpty()) {
            ExportedGate gate = egs.remove(0);
            switch(gate.getGateType()) {
                case UNIVERSAL:
                    for(int i = 0; i < kraus.size(); ++i) {
                        kraus.set(i,kraus.get(i).kronecker(gate.getInputMatrixes()[0]));
                    }
                    break;
                case POVM:
                    ArrayList<Matrix<Complex>> newChannel = new ArrayList<>();
                    for(int i = 0; i < kraus.size(); ++i) {
                        Matrix<Complex> k = kraus.get(i);
                        for(Matrix<Complex> kprime : gate.getInputMatrixes()) {
                            newChannel.add(k.kronecker(kprime));
                        }
                        //Rip garbage collector
                    }
                    kraus = newChannel;
                    break;
            }
        }
        return kraus;
    }

    private static Matrix<Complex> applyKrausDontLook(ArrayList<Matrix<Complex>> kraus, Matrix<Complex> density) {
        Matrix<Complex> newDensity = null;
        System.out.println("Density matrix in applyKrausDontLook");
        System.out.println(density);
        for(Matrix<Complex> k : kraus) {
            System.out.println("Kraus matrix");
            System.out.println(k);
            if(newDensity == null) {
                newDensity = k.mult(density);
                Matrix<Complex> kdagger = Matrix.map(Complex.ZERO(),k,Complex::conjugate).transpose();
                newDensity = newDensity.mult(kdagger);
            } else {
                newDensity = newDensity.add(k.mult(density).mult(Matrix.map(Complex.ZERO(),k,Complex::conjugate).transpose()));
            }
        }
        return newDensity;
    }

    private static Matrix<Complex> applyKrausLook(ArrayList<Matrix<Complex>> kraus, Matrix<Complex> density) {
        double rand = (new Random()).nextDouble();
        Matrix<Complex> state = null;
        int i = 0;
        while(rand > 0) {
            Matrix<Complex> k = kraus.get(i++);
            state = k.mult(density).mult(Matrix.map(Complex.ZERO(),k,Complex::conjugate).transpose());
            rand -= state.trace().abs();
        }
        return state;
    }

    private static ArrayList<ExportedGate> removeIdentityUnderGates(ArrayList<ExportedGate> gates) {
        ArrayList<Integer> regsCovered = new ArrayList<>();
        for(ExportedGate g : gates) {
            if(g.getGateRegister().length > 1) {
                for(int i = 0; i < g.getGateRegister().length; ++i) {
                    regsCovered.add(g.getGateRegister()[i]);
                }
            }
        }
        ArrayList<ExportedGate> newGates = new ArrayList<>();
        for(ExportedGate g : gates) {
            boolean redundant = false;
            for(Integer i : regsCovered) {
                if(g.getGateModel().getName().equalsIgnoreCase("Identity") && (g.getGateRegister()[0]==i)) {
                    redundant = true;
                    break;
                }
            }
            if(!redundant) {
                newGates.add(g);
            }
        }
        return newGates;
    }

}
