package top.mrxiaom.sweet.drops.func.entry;

public class RoundRound implements IRound {
    public static final RoundRound INSTANCE = new RoundRound();
    private RoundRound() {}
    @Override
    public int process(double num) {
        return (int) Math.round(num);
    }
}
