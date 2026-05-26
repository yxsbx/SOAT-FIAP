const platformFeeTiers = [
  {min: 0, max: 10000, rate: 0.10, label: '10%', nextStartsAt: 10000.01, nextRate: 0.08},
  {min: 10000.01, max: 15000, rate: 0.08, label: '8%', nextStartsAt: 15000.01, nextRate: 0.07},
  {min: 15000.01, max: 20000, rate: 0.07, label: '7%', nextStartsAt: 20000.01, nextRate: 0.05},
  {min: 20000.01, max: Infinity, rate: 0.05, label: '5%', nextStartsAt: null, nextRate: null},
];

export function calculatePlatformFee(grossRevenue) {
  const gross = Number(grossRevenue || 0);
  const tier = platformFeeTiers.find((item) => gross >= item.min && gross <= item.max) || platformFeeTiers.at(-1);
  const feeAmount = gross * tier.rate;

  return {
    gross,
    feeRate: tier.rate,
    feeRateLabel: tier.label,
    feeAmount,
    net: gross - feeAmount,
    nextTierGap: tier.nextStartsAt ? Math.max(0, tier.nextStartsAt - gross) : 0,
    nextTierLabel: tier.nextRate ? `${(tier.nextRate * 100).toFixed(0)}%` : 'menor taxa ativa',
  };
}
