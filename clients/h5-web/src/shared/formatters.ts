export function formatDuration(minutes: number) {
  const safeMinutes = Math.max(0, Number(minutes || 0));
  const hour = Math.floor(safeMinutes / 60);
  const minute = safeMinutes % 60;
  return `${hour}h${String(minute).padStart(2, "0")}m`;
}

export function difficultyLabel(value: string) {
  const labels: Record<string, string> = {
    BEGINNER: "新手友好",
    INTERMEDIATE: "轻中度徒步",
    ADVANCED: "进阶强度",
  };
  return labels[value] || value;
}

export function waypointTypeLabel(value: string) {
  const labels: Record<string, string> = {
    VIEWPOINT: "观景点",
    SUPPLY: "补给点",
    DANGER: "危险提醒",
    TRAILHEAD: "起点",
    ENDPOINT: "终点",
    FORK: "岔路口",
    REST: "休息点",
    CAMP: "营地点",
    PHOTO_SPOT: "拍摄机位",
  };
  return labels[value] || value;
}

export function routeStatusLabel(value: string) {
  const labels: Record<string, string> = {
    DRAFT: "草稿中",
    PENDING_REVIEW: "待审核",
    PUBLISHED: "已发布",
    REJECTED: "已退回",
    ARCHIVED: "已归档",
  };
  return labels[value] || value;
}

export function formatDate(value: string) {
  if (!value) {
    return "";
  }
  return value.slice(0, 16).replace("T", " ");
}
