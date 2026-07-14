import React, { useState, useEffect } from 'react';
import { useTheme } from '@context/ThemeContext';
import {
  ResponsiveContainer, PieChart, Pie, Cell, Legend, Tooltip,
  BarChart, Bar, XAxis, YAxis, CartesianGrid, AreaChart, Area
} from 'recharts';

// CSS Skeleton Component for loading state inside chart card
const ChartSkeleton = () => (
  <div className="w-full h-full flex flex-col justify-between p-4 animate-pulse">
    <div className="h-6 w-1/3 bg-text-secondary/15 rounded-md mb-4" />
    <div className="flex-1 w-full bg-text-secondary/10 rounded-xl flex items-center justify-center min-h-[160px]">
      <div className="w-24 h-24 rounded-full border-8 border-text-secondary/15 border-t-transparent animate-spin" />
    </div>
  </div>
);

// Empty State Component for charts
const ChartEmptyState = ({ title }) => (
  <div className="w-full h-full flex flex-col items-center justify-center p-8 text-center min-h-[220px]">
    <p className="text-lg font-bold text-text-primary mb-1">{title || 'No Data Available'}</p>
    <p className="text-sm text-text-secondary">We couldn't find any data to visualize right now.</p>
  </div>
);

// Hook to resolve theme CSS variables dynamically on mount and on theme toggle
function useDynamicThemeColors() {
  const { theme } = useTheme();
  const [colors, setColors] = useState({
    accent: '#0f766e',
    accentHover: '#115e59',
    textPrimary: '#0a0a0c',
    textSecondary: '#5c5c64',
    border: 'rgba(0,0,0,0.08)',
    surfaceElevated: '#ffffff',
    pieColors: ['#0f766e', '#5c5c64', '#f59e0b']
  });

  useEffect(() => {
    const handle = requestAnimationFrame(() => {
      const style = getComputedStyle(document.documentElement);
      const accent = style.getPropertyValue('--accent').trim() || '#0f766e';
      const textPrimary = style.getPropertyValue('--text-primary').trim() || '#0a0a0c';
      const textSecondary = style.getPropertyValue('--text-secondary').trim() || '#5c5c64';
      const border = style.getPropertyValue('--border-token').trim() || 'rgba(0,0,0,0.08)';
      const surfaceElevated = style.getPropertyValue('--surface-elevated').trim() || '#ffffff';

      // Pie chart slices: In Use (Accent), In Storage (Secondary text), Service (Warn amber)
      const pieColors = [
        accent,
        textSecondary,
        '#f59e0b' // Warning Amber
      ];

      setColors({
        accent,
        textPrimary,
        textSecondary,
        border,
        surfaceElevated,
        pieColors
      });
    });
    return () => cancelAnimationFrame(handle);
  }, [theme]);

  return colors;
}

// 1. DoughnutStat Component (Asset Allocation)
export const DoughnutStat = ({ data = [], isLoading, error }) => {
  const colors = useDynamicThemeColors();

  if (isLoading) return <ChartSkeleton />;
  if (error) return <ChartEmptyState title="Error Loading Allocation" />;
  if (!data || data.length === 0 || data.every(d => d.value === 0)) {
    return <ChartEmptyState title="No Asset Allocation Data" />;
  }

  return (
    <div className="w-full h-full min-h-[220px]">
      <ResponsiveContainer width="100%" height={220}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            innerRadius={60}
            outerRadius={80}
            paddingAngle={4}
            dataKey="value"
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={colors.pieColors[index % colors.pieColors.length]} />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{
              backgroundColor: colors.surfaceElevated,
              borderColor: colors.border,
              borderRadius: '12px',
              color: colors.textPrimary,
              fontFamily: 'inherit'
            }}
          />
          <Legend
            verticalAlign="bottom"
            height={36}
            formatter={(value) => <span className="text-xs font-semibold text-text-secondary">{value}</span>}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
};

// 2. StackedBarStat Component (Condition Health by Category)
export const StackedBarStat = ({ data = [], isLoading, error }) => {
  const colors = useDynamicThemeColors();

  if (isLoading) return <ChartSkeleton />;
  if (error) return <ChartEmptyState title="Error Loading Category Stats" />;
  if (!data || data.length === 0) return <ChartEmptyState title="No Category Data" />;

  // Map condition statuses to specific colors
  const conditionColors = {
    EXCELLENT: '#10b981', // Emerald-500
    GOOD: '#14b8a6',      // Teal-500
    FAIR: '#eab308',      // Amber-500
    BAD: '#f97316',       // Orange-500
    DAMAGED: '#ef4444'    // Red-500
  };

  return (
    <div className="w-full h-full min-h-[220px]">
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke={colors.border} vertical={false} />
          <XAxis
            dataKey="category"
            stroke={colors.textSecondary}
            fontSize={10}
            fontWeight="bold"
            tickLine={false}
          />
          <YAxis
            stroke={colors.textSecondary}
            fontSize={10}
            fontWeight="bold"
            tickLine={false}
            allowDecimals={false}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: colors.surfaceElevated,
              borderColor: colors.border,
              borderRadius: '12px',
              color: colors.textPrimary,
              fontFamily: 'inherit'
            }}
          />
          <Legend
            verticalAlign="bottom"
            height={36}
            formatter={(value) => <span className="text-[10px] font-black tracking-widest uppercase text-text-secondary">{value}</span>}
          />
          <Bar dataKey="EXCELLENT" stackId="a" fill={conditionColors.EXCELLENT} radius={[0, 0, 0, 0]} />
          <Bar dataKey="GOOD" stackId="a" fill={conditionColors.GOOD} radius={[0, 0, 0, 0]} />
          <Bar dataKey="FAIR" stackId="a" fill={conditionColors.FAIR} radius={[0, 0, 0, 0]} />
          <Bar dataKey="BAD" stackId="a" fill={conditionColors.BAD} radius={[0, 0, 0, 0]} />
          <Bar dataKey="DAMAGED" stackId="a" fill={conditionColors.DAMAGED} radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

// 3. TrendLineStat Component (Asset & Maintenance Volume Trend)
export const TrendLineStat = ({ data = [], isLoading, error }) => {
  const colors = useDynamicThemeColors();

  if (isLoading) return <ChartSkeleton />;
  if (error) return <ChartEmptyState title="Error Loading Trends" />;
  if (!data || data.length === 0) return <ChartEmptyState title="No Trend Data" />;

  return (
    <div className="w-full h-full min-h-[220px]">
      <ResponsiveContainer width="100%" height={220}>
        <AreaChart data={data} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <defs>
            <linearGradient id="colorMaint" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor={colors.accent} stopOpacity={0.4} />
              <stop offset="95%" stopColor={colors.accent} stopOpacity={0.0} />
            </linearGradient>
            <linearGradient id="colorAsset" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor={colors.textSecondary} stopOpacity={0.2} />
              <stop offset="95%" stopColor={colors.textSecondary} stopOpacity={0.0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke={colors.border} vertical={false} />
          <XAxis
            dataKey="month"
            stroke={colors.textSecondary}
            fontSize={10}
            fontWeight="bold"
            tickLine={false}
          />
          <YAxis
            stroke={colors.textSecondary}
            fontSize={10}
            fontWeight="bold"
            tickLine={false}
            allowDecimals={false}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: colors.surfaceElevated,
              borderColor: colors.border,
              borderRadius: '12px',
              color: colors.textPrimary,
              fontFamily: 'inherit'
            }}
          />
          <Legend
            verticalAlign="bottom"
            height={36}
            formatter={(value) => <span className="text-xs font-semibold text-text-secondary">{value}</span>}
          />
          <Area
            name="Repairs Performed"
            type="monotone"
            dataKey="maintenanceCount"
            stroke={colors.accent}
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorMaint)"
          />
          <Area
            name="Assets Added"
            type="monotone"
            dataKey="assetCount"
            stroke={colors.textSecondary}
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorAsset)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
};
