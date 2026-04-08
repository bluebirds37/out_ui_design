import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:route',
      order: 10,
      title: 'TrailNote 内容管理',
    },
    name: 'TrailNoteContent',
    path: '/trailnote',
    redirect: '/trailnote/routes',
    children: [
      {
        name: 'TrailNoteRoutes',
        path: '/trailnote/routes',
        component: () => import('#/views/route/index.vue'),
        meta: {
          icon: 'lucide:map',
          title: '路线管理',
        },
      },
      {
        name: 'TrailNoteComments',
        path: '/trailnote/comments',
        component: () => import('#/views/comment/index.vue'),
        meta: {
          icon: 'lucide:messages-square',
          title: '评论管理',
        },
      },
    ],
  },
];

export default routes;
