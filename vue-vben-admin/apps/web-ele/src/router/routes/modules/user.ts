import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'mdi:user',
      title: $t('page.user.title'),
    },
    name: 'User',
    path: '/user',
    redirect: '/user/index',
    children: [
      {
        name: 'UserIndex',
        path: '/user/index',
        component: () => import('#/views/user/index.vue'),
        meta: {
          icon: 'mdi:user',
          title: $t('page.user.index'),
        },
      },
    ],
  },
];

export default routes;
