package com.ovirt.reports.jasper;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;

public class ActiveOsTimelineChartCustomizer implements JRChartCustomizer {

    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
            long longerThanMonthInMiliseconds = 3064800000L;
            long longerThanDayInMiliseconds = 86500000L * 2;
            XYPlot categoryPlot = chart.getXYPlot();
            XYItemRenderer renderer = chart.getXYPlot().getRenderer();

            categoryPlot.setNoDataMessage("No Data Available");
            DateAxis domainaxis = (DateAxis) categoryPlot.getDomainAxis();
            if (domainaxis.getMaximumDate().getTime() - domainaxis.getMinimumDate().getTime() > longerThanDayInMiliseconds)
            {
                if (domainaxis.getMaximumDate().getTime() - domainaxis.getMinimumDate().getTime() < longerThanMonthInMiliseconds)
                {
                    domainaxis.setAutoTickUnitSelection(false);
                    domainaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,5,new SimpleDateFormat("dd MMM")));
                    domainaxis.setDateFormatOverride(new SimpleDateFormat("dd MMM"));
                    domainaxis.setLabelAngle(Math.PI / 2);
                    domainaxis.setLabelAngle(0);
                }
                else if (domainaxis.getMaximumDate().getTime() - domainaxis.getMinimumDate().getTime() < longerThanMonthInMiliseconds * 3)
                {
                domainaxis.setAutoTickUnitSelection(false);
                domainaxis.setTickUnit(new DateTickUnit(DateTickUnit.DAY,14,new SimpleDateFormat("dd MMM")));
                domainaxis.setDateFormatOverride(new SimpleDateFormat("dd MMM"));
                domainaxis.setLabelAngle(Math.PI / 2);
                domainaxis.setLabelAngle(0);
                }
            }
            domainaxis.setTickMarkPosition(DateTickMarkPosition.START);
            domainaxis.setTickMarksVisible(true);

            float[] unknowngary = new float[3];
            float[] knownblue = new float[3];
            float[] rhelred = new float[3];
            float[] windowsyellow = new float[3];
            float[] otherlinuxblue = new float[3];
            Color.RGBtoHSB(153, 153, 153, unknowngary);
            Color.RGBtoHSB(0, 0, 255, knownblue);
            Color.RGBtoHSB(204, 0, 0, rhelred);
            Color.RGBtoHSB(255, 204, 51, windowsyellow);
            Color.RGBtoHSB(0, 51, 2, otherlinuxblue);
            for (int i =1; i <= categoryPlot.getDataset().getSeriesCount(); i++)
            {
                if (categoryPlot.getDataset().getSeriesKey(i-1).toString().contains("Unknown OS".subSequence(0, 10)))
                {
                    categoryPlot.getRenderer().setSeriesPaint(i-1, Color.getHSBColor(unknowngary[0], unknowngary[1], unknowngary[2]));
                }
                if (categoryPlot.getDataset().getSeriesKey(i-1).toString().contains("Known OS".subSequence(0, 8)))
                {
                    categoryPlot.getRenderer().setSeriesPaint(i-1, Color.getHSBColor(knownblue[0], knownblue[1], knownblue[2]));
                }
                if (categoryPlot.getDataset().getSeriesKey(i-1).toString().contains("RHEL".subSequence(0, 4)))
                {
                    categoryPlot.getRenderer().setSeriesPaint(i-1, Color.getHSBColor(rhelred[0], rhelred[1], rhelred[2]));
                }
                if (categoryPlot.getDataset().getSeriesKey(i-1).toString().contains("Windows".subSequence(0, 7)))
                {
                    categoryPlot.getRenderer().setSeriesPaint(i-1, Color.getHSBColor(windowsyellow[0], windowsyellow[1], windowsyellow[2]));
                }
                if (categoryPlot.getDataset().getSeriesKey(i-1).toString().contains("Other Linux".subSequence(0, 11)))
                {
                    categoryPlot.getRenderer().setSeriesPaint(i-1, Color.getHSBColor(otherlinuxblue[0], otherlinuxblue[1], otherlinuxblue[2]));
                }
            }

            LegendItemCollection chartLegend = categoryPlot.getLegendItems();
            LegendItemCollection res = new LegendItemCollection();
            Shape square = new Rectangle2D.Double(0,0,5,5);
            for (int i = 0; i < chartLegend.getItemCount(); i++) {
               LegendItem item = chartLegend.get(i);
               String label = item.getLabel();
               if (label.trim() != "")
               {
                   res.add(new LegendItem(label, item.getDescription(), item.getToolTipText(), item.getURLText(), true, square, true, item.getFillPaint(), item.isShapeOutlineVisible(), item.getOutlinePaint(), item.getOutlineStroke(), false, item.getLine(), item.getLineStroke(), item.getLinePaint()));
               }
               else {renderer.setSeriesVisibleInLegend(item.getSeriesIndex(),false);}
            }
            categoryPlot.setFixedLegendItems(res);
            chart.getLegend().setFrame(BlockBorder.NONE);

            renderer.setBaseStroke(

                    new BasicStroke(2.0f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL)

                    );

            ValueAxis rangeAxis = categoryPlot.getRangeAxis();
            if (rangeAxis instanceof NumberAxis) {
                NumberAxis axis = (NumberAxis) rangeAxis;
                axis.setNumberFormatOverride(new DecimalFormat("###,###,###.#"));
                double upperBound = axis.getUpperBound();
                int a = (((int) upperBound / 10) * 10) + 10;
                upperBound = a;
                axis.setUpperBound(upperBound);
                double lowerBound = axis.getLowerBound();
                int a2 = (((int) lowerBound / 10) * 10) - 10;
                lowerBound = a2;
                if (lowerBound < 0)
                {
                    axis.setLowerBound(0);
                }
                else
                {
                    axis.setLowerBound(lowerBound);
                }
                axis.setAutoRangeMinimumSize(1.0);
            }
            rangeAxis.setTickLabelsVisible(true);
    }
}