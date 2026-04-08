export function formatDuration(minutes: number | undefined) {
  if (!minutes) {
    return "0h00m";
  }
  const hour = Math.floor(minutes / 60);
  const minute = minutes % 60;
  return `${hour}h${String(minute).padStart(2, "0")}m`;
}

export function difficultyLabel(difficulty: string | undefined) {
  const map: Record<string, string> = {
    BEGINNER: "新手友好",
    INTERMEDIATE: "中阶强度",
    ADVANCED: "高阶挑战",
  };
  return difficulty ? (map[difficulty] ?? difficulty) : "待评估";
}

export function formatDate(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString("zh-CN", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}
