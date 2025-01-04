package top.mrxiaom.sweet.drops.func.entry.round;

public class RoundFloor implements IRound {
    public static final RoundFloor INSTANCE = new RoundFloor();
    private RoundFloor() {}
    @Override
    public int process(double num) {
        return (int) Math.floor(num);
    }
}
