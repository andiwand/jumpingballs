package at.andiwand.jumpingballs.physic;

public abstract class ForceLink {

    MassPlot massPlotA;
    MassPlot massPlotB;

    public ForceLink(MassPlot massPlotA, MassPlot massPlotB) {
	this.massPlotA = massPlotA;
	this.massPlotB = massPlotB;
    }

    public MassPlot getMassPlotA() {
	return massPlotA;
    }

    public MassPlot getMassPlotB() {
	return massPlotB;
    }

    public abstract void update();

}